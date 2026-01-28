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
 // 1. 原本的：低庫存警報
    public void sendStockAlert(String productName, int currentStock) {
        String text = String.format("⚠️ 庫存告急警報！\n商品：%s\n剩餘庫存：%d\n請盡快補貨！", productName, currentStock);
        sendToLine(text); // 呼叫下面的共用方法
    }

    // 2. 🔥 新增：零庫存下架通知
    public void sendOutOfStockAlert(String productName) {
        String text = String.format("❌ 商品下架通知\n商品：%s\n庫存已歸零，系統已自動將其下架。", productName);
        sendToLine(text); // 呼叫下面的共用方法
    }

    // 3. 🛠️ 私有工具方法：負責處理 JSON 格式與發送 (大家都可以共用這段)
    private void sendToLine(String rawText) {
        // 處理特殊字元
        String safeText = rawText.replace("\n", "\\n").replace("\"", "\\\"");

        String jsonBody = "{"
                + "\"to\": \"" + adminUserId + "\","
                + "\"messages\": [{"
                + "\"type\": \"text\","
                + "\"text\": \"" + safeText + "\""
                + "}]"
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.line.me/v2/bot/message/push"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + channelToken)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() != 200) {
                        System.err.println("LINE 發送失敗: " + response.body());
                    }
                });
    }
}