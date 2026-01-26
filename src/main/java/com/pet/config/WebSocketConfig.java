package com.pet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//1. 標註這是一個設定檔，Spring 啟動時會來讀取
@Configuration 
//2. 啟用 WebSocket 的訊息代理人 (Message Broker) 功能
//簡單說就是開啟「幫忙轉信」的功能
@EnableWebSocketMessageBroker 
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

 @Override
 public void configureMessageBroker(MessageBrokerRegistry config) {
     // 設定「訂閱路徑」 (收信箱)
     // 當後端要把訊息傳給前端時，會放在這裡面
     // 前端只要訂閱 /topic/xxx，後端往這裡發信，前端就會收到
     config.enableSimpleBroker("/topic"); 
     
     // 設定「發送路徑」 (寄信口)
     // 當前端要傳訊息給後端時，必須加上這個前綴
     // 例如：前端要呼叫 ChatController 的方法，路徑是 /chat，
     // 那前端實際發送的路徑就要寫 /app/chat
     config.setApplicationDestinationPrefixes("/app"); 
 }

 @Override
 public void registerStompEndpoints(StompEndpointRegistry registry) {
     // 這是 WebSocket 的「大門」 (Handshake 握手連線點)
     // 因為 WebSocket 不是一般的 HTTP，前端要先跟這裡建立連線，才能開始聊天
     // 連線網址例如: ws://localhost:8081/ws-chat
     registry.addEndpoint("/shop/ws-chat")
             
             // 設定誰可以連線？ "*" 代表允許所有人
             // 開發階段通常全開，避免前端因為 CORS (跨域問題) 連不上
             .setAllowedOriginPatterns("*")
             
             // 這是為了相容性！有些舊瀏覽器不支援 WebSocket，
             // SockJS 會自動切換成 HTTP Polling (輪詢) 來模擬連線，確保不會斷線
             .withSockJS(); 
 }
}