package com.pet.controller.member;

import com.pet.dto.member.ChatMessageDTO;
import com.pet.model.member.ChatMessage;
import com.pet.service.member.ChatMessageService;
import com.pet.util.LoginUser;
import com.pet.service.member.AIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AIService aiService;

    // ================= REST API =================

    // 1. (前台) 取得我的歷史紀錄 (只回傳未隱藏的)
    @GetMapping("/shop/chat/history")
    public ResponseEntity<List<ChatMessage>> getMyHistory(@LoginUser Integer memberId) {
        return ResponseEntity.ok(chatService.getVisibleChatHistory(memberId));
    }

    // 2. (前台) 取得未讀數量
    @GetMapping("/shop/chat/unread")
    public ResponseEntity<Long> getMyUnreadCount(@LoginUser Integer memberId) {
        return ResponseEntity.ok(chatService.countUnreadForMember(memberId));
    }

    // 2.5 (前台) 標示已讀 (當會員打開視窗時呼叫)
    @PostMapping("/shop/chat/read")
    public ResponseEntity<Void> markMyMessagesAsRead(@LoginUser Integer memberId) {
        chatService.markAsReadForMember(memberId);
        return ResponseEntity.ok().build();
    }

    // ================= 後台 API =================

    // 3. (後台) 取得最近聊天列表
    @GetMapping("/admin/chat/recent")
    public ResponseEntity<List<ChatMessageDTO>> getRecentChats() {
        return ResponseEntity.ok(chatService.getRecentChatMembers());
    }

    // 4. (後台) 標示已讀
    @PostMapping("/admin/chat/read")
    public ResponseEntity<Void> markAsRead(@RequestParam Integer memberId) {
        chatService.markMessagesAsRead(memberId);
        return ResponseEntity.ok().build();
    }

    // 5. (後台) 取得特定會員的歷史紀錄 (給管理員看的)
    @GetMapping("/admin/chat/history")
    public ResponseEntity<List<ChatMessage>> getMemberHistory(@RequestParam Integer memberId) {
        return ResponseEntity.ok(chatService.getChatHistory(memberId));
    }

    // --- 新增：手動切換模式 API (可供前端按鈕呼叫) ---
    @PostMapping("/shop/chat/switchMode")
    public ResponseEntity<String> switchMode(@LoginUser Integer memberId, @RequestParam boolean humanMode) {
        chatService.setHumanMode(memberId, humanMode);
        return ResponseEntity.ok(humanMode ? "Switched to Human Mode" : "Switched to AI Mode");
    }

    // --- 新增：結束對話 API (清除歷史 + 重置 AI) ---
    @PostMapping("/shop/chat/end")
    public ResponseEntity<String> endSession(@LoginUser Integer memberId) {
        chatService.endSession(memberId);

        // 2. 通知後台管理員：使用者已離開 (並寫入資料庫，確保歷史紀錄看得到)
        ChatMessage sysMsg = chatService.saveMessage(memberId, "SYSTEM", "使用者已結束對話。");

        messagingTemplate.convertAndSend("/topic/admin", sysMsg);

        return ResponseEntity.ok("Session Ended. History Cleared.");
    }

    // ================= WebSocket API ( Payload 傳遞) =================

    // 1. 收信地址
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload Map<String, Object> payload) {

        // 2. 拆開信件內容 (@Payload)
        // 前端傳來的 JSON (信件內容) 當作 payload 進來
        // 我們把它拆解成 memberId, sender, content
        Integer memberId = Integer.parseInt(payload.get("memberId").toString());
        String sender = payload.get("sender").toString();
        String content = payload.get("content").toString();

        // 3. 存擋 (先把信影印一份存到資料庫)
        // 這樣使用者重新整理才看得到歷史紀錄
        ChatMessage savedMsg = chatService.saveMessage(memberId, sender, content);

        // 4. 分信 (郵差投遞)
        if ("MEMBER".equals(sender)) {
            // --- 情境 A：會員講話 ---

            // 動作：推播給「管理員」
            // 語法：messagingTemplate.convertAndSend(訂閱路徑, 訊息物件)
            // 這裡的意思是：把信丟到 "/topic/admin" 這個信箱
            // 因為所有管理員都在監聽這個信箱，所以他們都會收到通知！
            // 動作：推播給「管理員」 (僅在真人模式下通知，避免 AI 訊息洗版)
            if (chatService.isHumanMode(memberId)) {
                messagingTemplate.convertAndSend("/topic/admin", savedMsg);
            }

            // --- B. 判斷是否需要 AI 回覆 ---

            // 1. 關鍵字觸發切換
            if (content.contains("轉真人") || content.contains("真人客服")) {
                chatService.setHumanMode(memberId, true);

                // 系統自動回覆
                String sysMsg = "已為您轉接真人客服，請稍候，我們將盡快為您服務。";
                ChatMessage aiMsg = chatService.saveMessage(memberId, "AI", sysMsg);
                messagingTemplate.convertAndSend("/topic/member/" + memberId, aiMsg);
                messagingTemplate.convertAndSend("/topic/admin", aiMsg);
                return; // 結束，不呼叫 AI
            }

            // 2. 只有在「非真人模式」下，才呼叫 AI
            if (!chatService.isHumanMode(memberId)) {

                // 呼叫 Gemini
                String aiReplyContent = aiService.callGemini(memberId, content);

                ChatMessage aiMsg = chatService.saveMessage(memberId, "AI", aiReplyContent);
                messagingTemplate.convertAndSend("/topic/member/" + memberId, aiMsg);

                // 🔥 1. 先檢查是否需要切換模式
                if (aiReplyContent.contains("已為您轉接真人客服")) {
                    chatService.setHumanMode(memberId, true);
                }

                // 🔥 2. 再決定要不要推播給管理員 (只有真人模式才推)
                if (chatService.isHumanMode(memberId)) {
                    messagingTemplate.convertAndSend("/topic/admin", aiMsg);
                }
            }

        } else if ("ADMIN".equals(sender)) {
            // --- 情境 B：管理員/真人客服講話 ---

            // 0. 檢查：若使用者已結束對話，禁止傳送 (防止管理員騷擾)
            if (chatService.isSessionEnded(memberId)) {
                // 發送一個 SYSTEM 訊息回給 ADMIN (不存入資料庫，只推播給 Admin)
                ChatMessage errorMsg = ChatMessage.builder()
                        .memberId(memberId)
                        .sender("SYSTEM")
                        .content("【系統提示】使用者已結束對話，無法傳送訊息。")
                        .createdAt(LocalDateTime.now())
                        .build();
                // 這裡我們直接推給 Admin，假裝是一條新訊息，或者可以有特殊的處理
                // 因為 admin 訂閱的是 /topic/admin，所以所有管理員都會收到
                messagingTemplate.convertAndSend("/topic/admin", errorMsg);
                return;
            }

            // 動作：推播給「該位會員」
            // 這裡很關鍵！路徑是動態的："/topic/member/" + memberId
            // 修正：必須配合前台 ChatSupport.vue 訂閱的頻道名稱 (/topic/user/...)
            messagingTemplate.convertAndSend("/topic/member/" + memberId, savedMsg);

            // 管理員回話後，自動設定為真人模式
            chatService.setHumanMode(memberId, true);
        }
    }
}