package com.pet.service.order;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EcpayService {
	@Autowired
	private OrderService oService;

    // === 綠界測試環境參數 (Stage) ===
    // 正式上線時請切換為正式環境的 Key/IV 與 URL
    private final String MERCHANT_ID = "3002607";
    private final String HASH_KEY = "pwFHCqoQZGmho4w6";
    private final String HASH_IV = "EkRm7iFT261dpevs";
    private final String ACTION_URL = "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5";
    private final String BASE_URL ="https://unchid-technologically-pok.ngrok-free.dev";

    // 回傳網址 (請改為您的實際網址，本地開發需用 ngrok)
    // ReturnURL: 綠界背景呼叫，告知付款結果
    private final String RETURN_URL = BASE_URL+"/shop/checkout/callback";
    // ClientBackURL: 使用者付款完成後，點擊按鈕返回的網址
    private final String CLIENT_BACK_URL = "http://localhost:5173";
    
    // 綠界物流地圖 API (測試環境)
    private final String LOGISTICS_ACTION_URL = "https://logistics-stage.ecpay.com.tw/Express/map";
    
    // 地圖選完後，綠界 POST 回來的後端網址 (必須是 ngrok 外網)
    private final String MAP_CALLBACK_URL = BASE_URL+"/shop/checkout/map_callback";


    /**
     * 產生綠界金流表單 HTML
     * @param orderId 訂單編號
     * @param totalAmount 總金額
     * @param itemName 商品名稱 (例如: "寵物飼料 x1, 玩具 x2")
     * @return HTML Form 字串
     */
    public String createEcpayForm(String orderId, BigDecimal totalAmount, String itemName,String saveId) {
        // 使用 TreeMap 確保參數按 Key 的字母順序排列 (這是計算 CheckMacValue 的必要條件)
        Map<String, String> params = new TreeMap<>();

        // 1. 設定必要參數
        params.put("MerchantID", MERCHANT_ID);
        params.put("MerchantTradeNo", orderId);
        params.put("MerchantTradeDate", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(totalAmount.intValue()));
        params.put("TradeDesc", "PetShopOrder"); // 交易描述，盡量用英文或簡單中文
        params.put("ItemName", itemName.length() > 200 ? "Pet Shop Items" : itemName); // 限制長度
        params.put("ReturnURL", RETURN_URL);
        params.put("ClientBackURL", CLIENT_BACK_URL);
        params.put("ChoosePayment", "ALL"); // 預設全開 (信用卡/ATM/超商)
        params.put("EncryptType", "1"); // 固定為 1 (SHA256)
        params.put("CustomField1", saveId);

        // 2. 產生檢查碼 (CheckMacValue)
        String checkMacValue = generateCheckMacValue(params);
        params.put("CheckMacValue", checkMacValue);

        // 3. 組合 HTML 表單
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body>");
        html.append("<form id='ecpay-form' action='").append(ACTION_URL).append("' method='POST'>");
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            html.append("<input type='hidden' name='").append(entry.getKey())
                .append("' value='").append(entry.getValue()).append("'/>");
        }
        
        html.append("</form>");
        // 自動送出表單的 Script
        html.append("<script>document.getElementById('ecpay-form').submit();</script>");
        html.append("</body></html>");

        return html.toString();
    }

    /**
     * 計算 CheckMacValue
     * 邏輯：排序 -> 串接 Key/IV -> URL Encode -> 轉小寫 -> Replace 特殊字元 -> SHA256 -> 轉大寫
     */
    private String generateCheckMacValue(Map<String, String> params) {
        try {
            // 1. 參數串接 (Key=Value&Key=Value...)
            String queryString = params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            // 2. 頭尾加上 HashKey 與 HashIV
            String rawString = "HashKey=" + HASH_KEY + "&" + queryString + "&HashIV=" + HASH_IV;

            // 3. URL Encode (Java 預設會把空格轉為 +，需注意)
            String urlEncoded = URLEncoder.encode(rawString, StandardCharsets.UTF_8.name())
                    .toLowerCase(); // 綠界規定轉小寫

            // 4. 處理 Java URLEncoder 與 .NET 的差異 (綠界規定要替換這些字元)
            urlEncoded = urlEncoded.replace("%2d", "-")
                                   .replace("%5f", "_")
                                   .replace("%2e", ".")
                                   .replace("%21", "!")
                                   .replace("%2a", "*")
                                   .replace("%28", "(")
                                   .replace("%29", ")");

            // 5. SHA256 加密
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(urlEncoded.getBytes(StandardCharsets.UTF_8));

            // 6. 轉成大寫的 Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();

        } catch (Exception e) {
            throw new RuntimeException("Error generating CheckMacValue", e);
        }
    }
    /**
     * 處理綠界回調 (驗證 CheckMacValue 並更新訂單)
     */
    public String handleEcpayCallback(Map<String, String> params) {
        System.out.println("收到綠界回調: " + params);

        // 1. 取出綠界傳來的檢查碼
        String receivedMacValue = params.get("CheckMacValue");
        
        // 2. 驗證檢查碼
        // ⚠️ 重要：必須將 params 轉為 TreeMap 進行排序，並移除 CheckMacValue 欄位後重新計算
        // 因為 CheckMacValue 本身不參與加密計算
        Map<String, String> verifyParams = new TreeMap<>(params);
        verifyParams.remove("CheckMacValue");
        
        // 使用與產生訂單時相同的加密邏輯計算 (假設您在同一個 Service 內有此方法)
        String calculatedMacValue = generateCheckMacValue(verifyParams);

        // 比對是否一致
        if (!calculatedMacValue.equals(receivedMacValue)) {
            System.out.println("⚠️ CheckMacValue 驗證失敗！資料可能被竄改。");
            System.out.println("收到: " + receivedMacValue);
            System.out.println("計算: " + calculatedMacValue);
            // 驗證失敗，回傳錯誤給綠界 (綠界會視為失敗並可能重試)
            return "0|Error"; 
        }

        // 3. 檢查交易狀態 (RtnCode = 1 代表成功)
        String rtnCode = params.get("RtnCode");
        String merchantTradeNo = params.get("MerchantTradeNo"); // 您的訂單編號
        String rtnMsg = params.get("RtnMsg");
        // String tradeAmt = params.get("TradeAmt"); // 實際交易金額 (可選：再次驗證金額是否正確)

        if ("1".equals(rtnCode)) {
            // === 交易成功 ===
            System.out.println("✅ 訂單 " + merchantTradeNo + " 付款成功！");
            Integer saveId=Integer.valueOf(params.get("CustomField1"));
            // TODO: 請在此處呼叫您的 Repository 更新資料庫訂單狀態
            oService.updateOrderStatus(saveId,"付款完成");

            // 回傳 1|OK 告知綠界我們已成功接收 (這是綠界規定的標準成功回應)
            return "1|OK";
        } else {
            // === 交易失敗 ===
            System.out.println("❌ 訂單 " + merchantTradeNo + " 付款失敗，代碼: " + rtnCode + "，訊息: " + rtnMsg);
            
            // 視需求更新訂單狀態為「付款失敗」
            return "0|Error";
        }
    }
    public String createLogisticsForm(String shippingMethod) {
        Map<String, String> params = new TreeMap<>();

        // 1. 參數設定
        params.put("MerchantID", MERCHANT_ID);
        params.put("MerchantTradeNo", "MAP" + System.currentTimeMillis()); // 隨機產生即可
        params.put("LogisticsType", "CVS"); // 固定為超商
        params.put("LogisticsSubType", convertToLogisticsSubType(shippingMethod));
        params.put("IsCollection", "N"); // 是否代收貨款 (選門市而已，填 N)
        params.put("ServerReplyURL", MAP_CALLBACK_URL); // 伺服器端回傳 (雖然選地圖用不到，但綠界要求必填)
        
        // 關鍵：選完門市後，綠界會 POST 到這裡
        params.put("ClientReplyURL", MAP_CALLBACK_URL); 

        // 2. 計算 CheckMacValue (使用與金流相同的邏輯)
        // 注意：物流 API 的 CheckMacValue 計算邏輯與金流完全相同
        String checkMacValue = generateCheckMacValue(params);
        params.put("CheckMacValue", checkMacValue);

        // 3. 產生 HTML
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body>");
        html.append("<form id='ecpay-map-form' action='").append(LOGISTICS_ACTION_URL).append("' method='POST'>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            html.append("<input type='hidden' name='").append(entry.getKey())
                .append("' value='").append(entry.getValue()).append("'/>");
        }
        html.append("</form>");
        html.append("<script>document.getElementById('ecpay-map-form').submit();</script>");
        html.append("</body></html>");

        return html.toString();
    }

    // 轉換前端的物流名稱為綠界代碼
    private String convertToLogisticsSubType(String method) {
        if (method == null) return "UNIMART";
        switch (method) {
            case "7-11": return "UNIMART"; // 統一超商
            case "fami": return "FAMI";    // 全家
            case "hi-life": return "HILIFE"; // 萊爾富
            default: return "UNIMART";
        }
    }
}
