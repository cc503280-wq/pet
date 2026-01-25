package com.pet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration  //這是一個設定檔，啟動時請優先讀取我
@EnableWebSocketMessageBroker //啟用 WebSocket 的訊息代理人（Message Broker）功能
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) { //設定訊息的流向
	  // 設定 1: 出去的 (Server -> Client)
    config.enableSimpleBroker("/topic");
    // 設定 2: 進來的 (Client -> Server)
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) { //設定連線大門
    registry.addEndpoint("/ws-pet-grooming").setAllowedOriginPatterns("*").withSockJS(); //廣播大門
    //.addEndpoint("//ws-pet-grooming"):可自訂義地址
    //.setAllowedOriginPatterns("*"): 允許所有跨域連線
    //.withSockJS():WebSocket掛了，改用HTTP Long Polling (長輪詢)模擬連線
			    
    
    //前端設定   
    //import SockJS from 'sockjs-client/dist/sockjs'; 
	// 網址要對應後端的 Endpoint，盡量寫 http://，因為如果寫ws://，連線斷了就斷了
    // 寫http://WebSocket掛了，還有Http可以連線
	//const socket = new SockJS('http://localhost:8081/ws-pet-grooming');
		
	//人話:「我在 /ws-pet-grooming 開了一個門，不管你是誰都讓你進來 (AllowedOriginPatterns)，
	//而且就算你的路況不好（不支援 WebSocket），我也會想辦法幫你接通 (withSockJS)。」
  }

}