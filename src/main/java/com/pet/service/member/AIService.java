package com.pet.service.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pet.model.appointment.ServiceItem;
import com.pet.model.appointment.Groomer;
import com.pet.model.member.Coupon;
import com.pet.model.product.Product;
import com.pet.service.appointment.GroomerService;
import com.pet.service.appointment.ServiceItemService;
import com.pet.service.product.ProductService;
import com.pet.service.member.CouponService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIService {

        @Value("${gemini.api.key}")
        private String apiKey;

        @Value("${gemini.api.url}")
        private String apiUrl;

        private final RestTemplate restTemplate = new RestTemplate();
        private final ObjectMapper objectMapper = new ObjectMapper();

        // 注入四大天王 Service
        @Autowired
        private ServiceItemService serviceItemService;
        @Autowired
        private ProductService productService;
        @Autowired
        private CouponService couponService;
        @Autowired
        private GroomerService groomerService;

        public String callGemini(String userMessage) {
                try {
                        String url = apiUrl + "?key=" + apiKey;
                        StringBuilder contextBuilder = new StringBuilder();

                        // 1. 【常駐資訊】美容服務價目表
                        List<ServiceItem> services = serviceItemService.getAllActiveServiceItems();
                        String servicesInfo = services.stream()
                                        .map((ServiceItem s) -> String.format("- %s (價格: $%s, 適合: %s %s)",
                                                        s.getServiceName(),
                                                        s.getPrice(),
                                                        s.getTargetPetType(), // 修正: getPetType -> getTargetPetType
                                                        s.getTargetPetSize() // 修正: getPetSize -> getTargetPetSize
                                        ))
                                        .collect(Collectors.joining("\n"));
                        contextBuilder.append("【美容服務價目表】:\n").append(servicesInfo).append("\n\n");

                        // 2. 【動態資訊】依照關鍵字決定要不要撈資料

                        // A. 商品搜尋
                        if (userMessage.contains("買") || userMessage.contains("推薦") || userMessage.contains("飼料") ||
                                        userMessage.contains("罐頭") || userMessage.contains("貓砂")
                                        || userMessage.contains("玩具") || userMessage.contains("多少錢")) {

                                List<Product> products = productService.searchProducts(userMessage);
                                if (products.isEmpty()) {
                                        products = productService.findActiveProducts();
                                }
                                String productInfo = products.stream().limit(10)
                                                .map((Product p) -> String.format("- %s ($%s, 庫存: %s)",
                                                                p.getProductName(), p.getPrice(), p.getStock()))
                                                .collect(Collectors.joining("\n"));
                                contextBuilder.append("【相關商品推薦】:\n").append(productInfo).append("\n\n");
                        }

                        // B. 優惠券
                        if (userMessage.contains("優惠") || userMessage.contains("折扣") || userMessage.contains("便宜")) {
                                List<Coupon> coupons = couponService.getAvailableCoupons();
                                String couponInfo = coupons.stream()
                                                .map((Coupon c) -> {
                                                        // 修正: Coupon 沒有 name，改由折扣規則自動生成描述
                                                        String desc = "";
                                                        if ("percent".equals(c.getDiscountType())) {
                                                                int off = (int) (c.getDiscountValue() * 100);
                                                                // 例如 0.8 -> 80 (8折), 0.85 -> 85 (85折)
                                                                // 轉成中文習慣: 0.8 -> 8折, 0.5 -> 5折
                                                                // 這裡簡單處理: 直接顯示 "XX折" (例如 80% Off) 或直接用中文 "X折"
                                                                // 這裡寫簡單邏輯: 0.9 -> 9折
                                                                int discount = (int) (c.getDiscountValue() * 10);
                                                                desc = discount + "折優惠";
                                                        } else {
                                                                desc = "折抵 $" + c.getDiscountValue().intValue();
                                                        }
                                                        return String.format("- 代碼[%s]: %s (低消 $%s)", c.getCode(), desc,
                                                                        c.getMinPurchase());
                                                })
                                                .collect(Collectors.joining("\n"));
                                contextBuilder.append("【目前可領取的優惠券】:\n").append(couponInfo).append("\n\n");
                        }

                        // C. 美容師
                        if (userMessage.contains("美容師") || userMessage.contains("預約") || userMessage.contains("剪毛")) {
                                List<Groomer> groomers = groomerService.getAllGroomer();
                                String groomerInfo = groomers.stream()
                                                .filter((Groomer g) -> g.getIsActive() != null && g.getIsActive())
                                                .map((Groomer g) -> String.format("- %s (年資: %s)", g.getGroomerName(),
                                                                g.getHiredate()))
                                                .collect(Collectors.joining("\n"));
                                contextBuilder.append("【我們的專業美容師團隊】:\n").append(groomerInfo).append("\n\n");
                        }

                        // 3. 組合 System Prompt (開場白優化)
                        String systemPrompt = String.format("""
                                        你是寵物電商『MaoMaoLand』的智能客服 AI 助理。請用繁體中文、親切可愛的語氣(🐶, 🐱)回答。

                                        【重要：自我介紹】
                                        **請在回答的第一句話，或適當時機，向使用者表明你是一個「AI 智能助理」。**
                                        **若遇到無法處理的問題，請務必引導使用者輸入『真人客服』或『轉真人』以切換專人服務。**

                                        【網站基礎規範 (必讀)】：
                                        1. 免運政策：消費滿 $1,000 即享免運。
                                        2. 會員幣：每筆訂單回饋 1%% 作為會員幣，可無上限折抵。
                                        3. 修改資料/寵物/優惠券：請點擊右上方「會員中心圖示」進入「我的基本資料」或對應分頁。
                                        4. 查詢訂單/收藏：請至「會員中心」>「購物訂單」或「收藏清單」。

                                        %s

                                        【回答守則】：
                                        1. 請優先根據上方的【資訊卡】回答問題。
                                        2. 若商品沒庫存，請誠實告知。
                                        3.若詢問「訂單進度」或「個人隱私」，請引導至網站後台查詢，或輸入『真人客服』。
                                        4. 通用的寵物知識 (如：狗不能吃什麼?)，請直接依照你的知識庫回答。

                                        客戶的問題是：%s
                                        """, contextBuilder.toString(), userMessage);

                        // --- 呼叫 API ---
                        // 1. 建立最外層的 { }
                        ObjectNode rootNode = objectMapper.createObjectNode();

                        // 2. 建立 "contents": [ ... ]
                        ArrayNode contentsNode = rootNode.putArray("contents");

                        // 3. 建立陣列裡的第一個物件 { ... }
                        ObjectNode contentNode = contentsNode.addObject();

                        // 4. 建立 "parts": [ ... ]
                        ArrayNode partsNode = contentNode.putArray("parts");

                        // 5. 把你的問題塞進去: { "text": "..." }
                        partsNode.addObject().put("text", systemPrompt);

                        // 6. 設定 Headers，告訴 Google 我們寄過去的是 JSON 格式
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        // 7. 把信紙 (JSON內容) 和 信封設定 (Headers) 裝在一起變成一個包裹 (Entity)
                        HttpEntity<String> request = new HttpEntity<>(rootNode.toString(), headers);

                        // 8. 透過 restTemplate (瀏覽器) 發送 POST 請求
                        // 參數: 網址, 包裹, 回傳的這種類型(String)
                        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                        // 9. 解析回傳結果
                        // 把回傳的字串翻譯成樹狀結構
                        JsonNode responseJson = objectMapper.readTree(response.getBody());

                        // 像剝洋蔥一樣，一層一層拿：
                        return responseJson.path("candidates").get(0)
                                        .path("content").path("parts").get(0)
                                        .path("text").asText();

                } catch (Exception e) {
                        e.printStackTrace();
                        return "抱歉，AI 大腦運轉過熱中... 請稍後再試 😵";
                }
        }
}