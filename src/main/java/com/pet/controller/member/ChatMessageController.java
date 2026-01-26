package com.pet.controller.member;

import com.pet.dto.member.ChatMessageDTO;
import com.pet.model.member.ChatMessage;
import com.pet.service.member.ChatMessageService;
import com.pet.util.LoginUser;

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

    // ================= WebSocket API ( Payload 傳遞) =================

    // 1. 收信地址
    // 前端發過來的路徑是 /app/sendMessage
    // (因為 WebSocketConfig 設定了 /app 前綴，這裡只要寫 /sendMessage)
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
            messagingTemplate.convertAndSend("/topic/admin", savedMsg);

        } else if ("ADMIN".equals(sender)) {
            // --- 情境 B：管理員/真人客服講話 ---

            // 動作：推播給「該位會員」
            // 這裡很關鍵！路徑是動態的："/topic/user/" + memberId
            // 修正：必須配合前台 ChatSupport.vue 訂閱的頻道名稱 (/topic/user/...)
            messagingTemplate.convertAndSend("/topic/member/" + memberId, savedMsg);
        }
    }
}