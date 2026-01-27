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

    // 1. (前台) 取得我的歷史紀錄
    @GetMapping("/shop/chat/history")
    public ResponseEntity<List<ChatMessage>> getMyHistory(@LoginUser Integer memberId) {
        return ResponseEntity.ok(chatService.getChatHistory(memberId));
    }

    // 2. (前台) 取得未讀數量
    @GetMapping("/shop/chat/unread")
    public ResponseEntity<Long> getMyUnreadCount(@LoginUser Integer memberId) {
        return ResponseEntity.ok(chatService.countUnreadForMember(memberId));
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

    // ================= WebSocket API ( Payload 傳遞) =================

    // 1. 收信地址
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload Map<String, Object> payload) {

        Integer memberId = Integer.parseInt(payload.get("memberId").toString());
        String sender = payload.get("sender").toString();
        String content = payload.get("content").toString();

        // 3. 存擋
        ChatMessage savedMsg = chatService.saveMessage(memberId, sender, content);

        // 4. 分信
        if ("MEMBER".equals(sender)) {
            // --- 情境 A：會員講話 ---

            // A. 推播給管理員
            messagingTemplate.convertAndSend("/topic/admin", savedMsg);
            
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
                String aiReplyContent = aiService.callGemini(content);
                
                ChatMessage aiMsg = chatService.saveMessage(memberId, "AI", aiReplyContent);
                messagingTemplate.convertAndSend("/topic/member/" + memberId, aiMsg);
                messagingTemplate.convertAndSend("/topic/admin", aiMsg);
            }

        } else if ("ADMIN".equals(sender)) {
            // --- 情境 B：管理員/真人客服講話 ---

            // 推播給該位會員
            messagingTemplate.convertAndSend("/topic/member/" + memberId, savedMsg);
            
            // 管理員回話後，自動設定為真人模式
            chatService.setHumanMode(memberId, true); 
        }
    }
}