package com.pet.service.product; // 記得改成您的 package 名稱

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class LineNotificationServiceForAdmin {

    // ==========================================
    // ⚠️ 請填入您剛剛在開發者後台取得的資訊
    // ==========================================
	@Value("${line.bot.admin-channel-token}")
    private String channelToken;

    @Value("${line.bot.admin-user-id}")
    private String adminUserId;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * 發送庫存警報
     */
    public void sendStockAlert(String productName, int currentStock) {
        // 3. 準備訊息內容
    	String rawText = String.format("⚠️ 庫存告急警報！\n商品：%s\n剩餘庫存：%d\n請盡快補貨！", productName, currentStock);

        // 🔥【關鍵修正】把 "Java換行" 替換成 "JSON換行(\\n)"，並且處理雙引號
        String safeText = rawText.replace("\n", "\\n").replace("\"", "\\\"");

        // 2. 拼接 JSON 字串 (使用處理過的 safeText)
        String jsonBody = "{"
                + "\"to\": \"" + adminUserId + "\","
                + "\"messages\": [{"
                + "\"type\": \"text\","
                + "\"text\": \"" + safeText + "\"" // 👈 這裡改用 safeText
                + "}]"
                + "}";

        // 建立 HTTP 請求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.line.me/v2/bot/message/push"))
                .header("Content-Type", "application/json")
                // 使用 this.channelToken 替代原本的大寫常數
                .header("Authorization", "Bearer " + channelToken) 
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        // 發送
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    // 印出狀態碼檢查 (200 代表成功)
                    if (response.statusCode() == 200) {
                        System.out.println("LINE 警報發送成功！");
                    } else {
                        System.err.println("LINE 發送失敗，代碼: " + response.statusCode());
                        System.err.println("回應內容: " + response.body());
                    }
                })
                .exceptionally(e -> {
                    System.err.println("LINE 連線錯誤: " + e.getMessage());
                    return null;
                });
    }
}