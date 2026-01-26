package com.pet.controller.member;

import com.pet.dto.member.ChatMessageDTO;
import com.pet.model.member.ChatMessage;
import com.pet.service.member.ChatMessageService;
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

    // 💡 這個大家容易忘記：這是 Spring 提供的「廣播器」，用來主動把訊息推給前端
    @Autowired
    private SimpMessagingTemplate messagingTemplate; 

    // ================= REST API (歷史紀錄、未讀數) =================

    // 1. (前台) 取得我的歷史紀錄
    // 用法: GET /shop/chat/history?memberId=1
    @GetMapping("/shop/chat/history")
    public ResponseEntity<List<ChatMessage>> getMyHistory(@RequestParam Integer memberId) {
        return ResponseEntity.ok(chatService.getChatHistory(memberId));
    }

    // 2. (前台) 取得未讀數量 (小鈴鐺用)
    @GetMapping("/shop/chat/unread")
    public ResponseEntity<Long> getMyUnreadCount(@RequestParam Integer memberId) {
        return ResponseEntity.ok(chatService.countUnreadForMember(memberId));
    }

    // 3. (後台) 取得最近聊天列表 (管理員收件匣)
    @GetMapping("/shop/admin/chat/recent")
    public ResponseEntity<List<ChatMessageDTO>> getRecentChats() {
        return ResponseEntity.ok(chatService.getRecentChatMembers());
    }

    // 4. (後台) 標示已讀 (當管理員點進聊天室時呼叫)
    @PostMapping("/shop/admin/chat/read")
    public ResponseEntity<Void> markAsRead(@RequestParam Integer memberId) {
        chatService.markMessagesAsRead(memberId);
        return ResponseEntity.ok().build();
    }

    // ================= WebSocket API (即時傳訊) =================

    /**
     * 接收前端發送的訊息
     * 前端發送路徑: /app/sendMessage (因為 WebSocketConfig 設定了 /app 前綴)
     * 
     * @param payload 前端傳來的 JSON，例如: {"memberId": 1, "sender": "MEMBER", "content": "你好"}
     */
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload Map<String, Object> payload) {
        // 解析前端傳來的資料
        Integer memberId = Integer.parseInt(payload.get("memberId").toString());
        String sender = payload.get("sender").toString(); // "MEMBER" 或 "ADMIN"
        String content = payload.get("content").toString();

        // 1. 先存入資料庫
        ChatMessage savedMsg = chatService.saveMessage(memberId, sender, content);

        // 2. 判斷要推播給誰
        if ("MEMBER".equals(sender)) {
            // A. 如果是【會員】發的 -> 推播給【管理員】
            // 管理員訂閱的路徑: /topic/admin
            messagingTemplate.convertAndSend("/topic/admin", savedMsg);
            
            // --- 🤖 AI 伏筆 ---
            // 下一階段我們會在這裡加上：
            // if (是 AI 模式) { 呼叫 Gemini 並自動回覆(); }

        } else if ("ADMIN".equals(sender)) {
            // B. 如果是【管理員】發的 -> 推播給【該位會員】
            // 會員訂閱的路徑: /topic/user/{memberId}
            messagingTemplate.convertAndSend("/topic/user/" + memberId, savedMsg);
        }
    }
}