package com.pet.service.appointment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.pet.model.appointment.Appointment;

import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

/**
 * TwilioSmsService: 使用 Twilio REST API 發送簡訊通知
 * 用途：當寵物美容服務完成時，發送 SMS 通知會員
 * 
 */
@Service
@Slf4j
public class TwilioSmsService {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.phone-number:}")
    private String twilioPhoneNumber;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 發送服務完成簡訊通知
     * @param toPhone 會員的手機號碼 (需含國碼，如 +886912345678)
     * @param appointment 預約資料
     */
    @Async
    public void sendServiceCompletedSms(String toPhone, Appointment appointment) {
        if (accountSid == null || accountSid.isEmpty()) {
            log.error("Twilio Account SID 未設定，請檢查 application.properties");
            return;
        }
        if (authToken == null || authToken.isEmpty()) {
            log.error("Twilio Auth Token 未設定，請檢查 application.properties");
            return;
        }
        if (twilioPhoneNumber == null || twilioPhoneNumber.isEmpty()) {
            log.error("Twilio 電話號碼未設定，請檢查 application.properties");
            return;
        }

        try {
            // 組合簡訊內容
            String messageBody = String.format(
                "【MaoMaoLand通知】您的毛孩美容服務已完成！🐾\n" +
                "預約單號: %d\n" +
                "請至店內接回您的寶貝！",
                appointment.getAppointmentId()
            );

            // 處理台灣手機號碼格式
            String formattedPhone = formatPhoneNumber(toPhone);
            log.info("準備發送簡訊至: {}", formattedPhone);

            // Twilio REST API URL
            String url = String.format(
                "https://api.twilio.com/2010-04-01/Accounts/%s/Messages.json",
                accountSid
            );

            // 設定 HTTP Headers (Basic Auth)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String auth = accountSid + ":" + authToken;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);

            // 設定請求參數
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("To", formattedPhone);
            params.add("From", twilioPhoneNumber);
            params.add("Body", messageBody);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // 發送 POST 請求
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("簡訊發送成功！發送至: {}", formattedPhone);
            } else {
                log.error("簡訊發送失敗。Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            }

        } catch (Exception e) {
            log.error("發送簡訊失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 格式化台灣手機號碼
     * 0912345678 -> +886912345678
     */
    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }
        
        // 移除所有空格和 dash
        phone = phone.replaceAll("[\\s-]", "");
        
        // 如果已經有 + 開頭，直接回傳
        if (phone.startsWith("+")) {
            return phone;
        }
        
        // 台灣手機號碼轉換 (0912... -> +886912...)
        if (phone.startsWith("09") && phone.length() == 10) {
            return "+886" + phone.substring(1);
        }
        
        // 其他情況，假設是台灣號碼
        if (phone.startsWith("0")) {
            return "+886" + phone.substring(1);
        }
        
        return "+886" + phone;
    }
}
