package com.pet.service.appointment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.pet.model.appointment.Appointment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LineNotificationService: 負責發送 LINE 通知
 * 用途：當發生重要事件 (如：臨時取消預約) 時，主動推播訊息給管理員。
 */
@Service
@Slf4j
public class LineNotificationService {

    // 從 application.properties讀取 LINE Bot 的 Channel Token
    @Value("${line.bot.channel-token:}")
    private String channelToken;

    // 從 application.properties讀取管理員的 User ID (接收通知者)
    @Value("${line.manager.user-id:}")
    private String managerUserId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // LINE Messaging API - Push Message Endpoint
    private static final String LINE_PUSH_API = "https://api.line.me/v2/bot/message/push";

    /**
     * 發送預約取消通知
     * 當顧客在預約時間前 6 小時內取消時觸發
     */
    public void sendCancellationNotification(Appointment appointment, String groomerName) {
        if (channelToken == null || channelToken.isEmpty()) {
            log.error("LINE Channel Token 遺失。請檢查 'line.bot.channel-token' 設定。");
            return;
        }
        if (managerUserId == null || managerUserId.isEmpty()) {
            log.error("Manager User ID 遺失。請檢查 'line.manager.user-id' 設定。");
            return;
        }

        try {
            log.info("正在發送 LINE 通知 - 預約單號: {}, 接收者: {}", appointment.getAppointmentId(), managerUserId);

            // 1. 建構訊息內容
            String messageText = String.format(
                "【會員預約取消通知】\n" +
                "預約單號: %d\n" +
                "日期: %s\n" +
                "時間: %s\n" +
                "美容師: %s\n" +
                "原因: 會員於服務前 6 小時內取消服務，請留意。",
                appointment.getAppointmentId(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                groomerName
            );

            // 2. 建構 JSON Body (符合 LINE Messaging API 格式)
            Map<String, Object> textMessage = new HashMap<>();
            textMessage.put("type", "text");
            textMessage.put("text", messageText);

            Map<String, Object> body = new HashMap<>();
            body.put("to", managerUserId);
            body.put("messages", List.of(textMessage));

            String jsonBody = objectMapper.writeValueAsString(body);

            // 3. 設定 HTTP Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + channelToken);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            // 4. 發送 HTTP POST 請求
            ResponseEntity<String> response = restTemplate.postForEntity(LINE_PUSH_API, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("LINE 通知發送成功。");
            } else {
                log.error("LINE 通知發送失敗。Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            }

        } catch (Exception e) {
            log.error("透過 HTTP 發送 LINE 通知時發生錯誤", e);
        }
    }
}
