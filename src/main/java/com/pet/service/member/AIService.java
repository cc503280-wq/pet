package com.pet.service.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pet.model.appointment.ServiceItem;
import com.pet.model.appointment.Groomer;
import com.pet.model.member.Coupon;
import com.pet.model.order.Order;
import com.pet.model.product.Category;
import com.pet.model.product.Product;
import com.pet.service.appointment.GroomerService;
import com.pet.service.appointment.ServiceItemService;
import com.pet.service.product.ProductService;
import com.pet.service.member.CouponService;
import com.pet.service.appointment.AppointmentService; // 新增 AppointmentService

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException; // 新增這個 import
import org.springframework.web.client.RestTemplate;

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
    @Autowired
    private com.pet.service.order.OrderService orderService;
    @Autowired
    private AppointmentService appointmentService; // 新增 AppointmentService 注入

    // --- 關鍵字定義 (同義詞庫) ---
    private static final List<String> PRODUCT_KEYWORDS = List.of("買", "推薦", "飼料", "罐頭", "貓砂", "玩具", "多少錢", "價格", "費用",
            "cost", "price", "shop", "store", "東西");
    private static final List<String> COUPON_KEYWORDS = List.of("優惠", "折扣", "便宜", "coupon", "discount", "promotion",
            "代碼", "code");
    private static final List<String> GROOMER_KEYWORDS = List.of("美容", "預約", "剪毛", "洗澡", "groomer", "cut", "hair",
            "salon");
    private static final List<String> ORDER_KEYWORDS = List.of("訂單", "進度", "出貨", "包裹", "order", "status", "track",
            "shipping", "history");
    private static final List<String> APPOINTMENT_KEYWORDS = List.of("預約紀錄", "美容紀錄", "我的預約", "查詢預約",
            "appointment history", "booking status"); // 新增關鍵字

    /**
     * 呼叫 Gemini API
     * * @param memberId 會員ID (可為 null，代表未登入或無法取得)
     * 
     * @param userMessage 使用者輸入的訊息
     */
    public String callGemini(Integer memberId, String userMessage) {

        System.out.println("🔥 收到請求！準備呼叫 Google... 時間：" + System.currentTimeMillis());
        try {
            String url = apiUrl + "?key=" + apiKey;
            // System.out.println("正在呼叫 Gemini API: " + url); // Debug用
            StringBuilder contextBuilder = new StringBuilder();
            String lowerMsg = userMessage.toLowerCase(); // 統一轉小寫比對

            // 1. 【常駐資訊】美容服務價目表
            List<ServiceItem> services = serviceItemService.getAllActiveServiceItems();
            if (services == null)
                services = List.of();

            String servicesInfo = services.stream()
                    .map(s -> String.format("- %s (價格: $%s, 適合: %s %s)",
                            s.getServiceName(), s.getPrice(), s.getTargetPetType(), s.getTargetPetSize()))
                    .collect(Collectors.joining("\n"));
            contextBuilder.append("【美容服務價目表】:\n").append(servicesInfo).append("\n\n");

            // 2. 【動態資訊】依照關鍵字決定要不要撈資料

            // A. 商品搜尋
            // 策略 1: 先檢查是否包含「分類名稱」 (精準推薦)
            List<Category> allCategories = productService.getAllCategories();
            boolean categoryFound = false;

            for (Category cat : allCategories) {
                if (lowerMsg.contains(cat.getCategoryName().toLowerCase())) {
                    List<Product> catProducts = productService.getProductsByCategory(cat.getCategoryId());
                    if (catProducts != null && !catProducts.isEmpty()) {
                        String catInfo = catProducts.stream().limit(5) // 每個分類最多推 5 個
                                .map(p -> String.format("- %s ($%s, 庫存: %s)", p.getProductName(), p.getPrice(),
                                        p.getStock()))
                                .collect(Collectors.joining("\n"));
                        contextBuilder.append("【").append(cat.getCategoryName()).append("推薦】:\n").append(catInfo)
                                .append("\n\n");
                        categoryFound = true;
                    }
                }
            }

            // 策略 2: 如果沒找到特定分類，才用關鍵字模糊搜尋
            // 或者：如果使用者明確問「推薦」，也跑一下關鍵字搜尋
            if (!categoryFound || containsAny(lowerMsg, PRODUCT_KEYWORDS)) {
                List<Product> products = productService.searchProducts(userMessage);

                // 如果關鍵字搜尋沒東西，但有提到「商品」關鍵字，就推熱門商品 (Active Products)
                // 注意：這裡如果不設限，searchProducts 可能回傳空
                if ((products == null || products.isEmpty()) && containsAny(lowerMsg, PRODUCT_KEYWORDS)) {
                    products = productService.findActiveProducts();
                }

                if (products != null && !products.isEmpty()) {
                    // 過濾掉已經在分類推薦裡出現過的 (簡單做: 這裡就不特別過濾了，重複出現也無妨，當作強調)
                    String productInfo = products.stream().limit(10)
                            .map(p -> String.format("- %s ($%s, 庫存: %s)", p.getProductName(), p.getPrice(),
                                    p.getStock()))
                            .collect(Collectors.joining("\n"));

                    // 只有當真的有東西時才 append，避免標題空空的
                    contextBuilder.append("【相關商品搜尋結果】:\n").append(productInfo).append("\n\n");
                }
            }

            // B. 優惠券
            if (containsAny(lowerMsg, COUPON_KEYWORDS)) {
                List<Coupon> coupons = couponService.getAvailableCoupons();
                if (coupons == null)
                    coupons = List.of();

                String couponInfo = coupons.stream()
                        .map(c -> {
                            String desc = "percent".equals(c.getDiscountType())
                                    ? (int) (c.getDiscountValue() * 10) + "折"
                                    : "折抵 $" + c.getDiscountValue().intValue();
                            return String.format("- 代碼[%s]: %s (低消 $%s)", c.getCode(), desc, c.getMinPurchase());
                        })
                        .collect(Collectors.joining("\n"));
                contextBuilder.append("【目前可領取的優惠券】:\n").append(couponInfo).append("\n\n");
            }

            // C. 美容師
            if (containsAny(lowerMsg, GROOMER_KEYWORDS)) {
                List<Groomer> groomers = groomerService.getAllGroomer();
                if (groomers == null)
                    groomers = List.of();

                String groomerInfo = groomers.stream()
                        .filter(g -> Boolean.TRUE.equals(g.getIsActive()))
                        .map(g -> String.format("- %s (年資: %s)", g.getGroomerName(), g.getHiredate()))
                        .collect(Collectors.joining("\n"));
                contextBuilder.append("【我們的專業美容師團隊】:\n").append(groomerInfo).append("\n\n");
            }

            // D. 訂單查詢
            if (memberId != null && containsAny(lowerMsg, ORDER_KEYWORDS)) {
                List<Order> orders = orderService.getOrderByMemberId(memberId);
                if (orders != null && !orders.isEmpty()) {
                    // 取最近 3 筆訂單
                    String orderInfo = orders.stream()
                            .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate())) // 時間新->舊
                            .limit(3)
                            .map(o -> String.format("- 訂單號[%s] 金額$%s (狀態: %s) 日期: %s",
                                    o.getOrderId(), o.getTotalAmountDiscountPoints(), o.getStatus(),
                                    o.getOrderDate()))
                            .collect(Collectors.joining("\n"));
                    contextBuilder.append("【您最近的訂單紀錄 (僅本人可見)】:\n").append(orderInfo).append("\n\n");
                } else {
                    contextBuilder.append("【訂單查詢結果】: 您目前沒有歷史訂單或是查無資料。\n\n");
                }
            } else if (memberId == null && containsAny(lowerMsg, ORDER_KEYWORDS)) {
                contextBuilder.append("【系統提示】: 使用者詢問訂單，但目前似乎未登入或無法取得身分，請引導他登入後再試。\n\n");
            }

            // E. 美容預約查詢 (新增功能)
            if (memberId != null && containsAny(lowerMsg, APPOINTMENT_KEYWORDS)) {
                List<com.pet.model.appointment.AppointmentList> appointments = appointmentService
                        .getAppointmentsByMemberId(memberId);

                if (appointments != null && !appointments.isEmpty()) {
                    // 這邊取「未來」或「最近」的預約比較有意義，這邊簡單做：依照日期排序 (新->舊) 取前 3 筆
                    String apptInfo = appointments.stream()
                            .sorted((a1, a2) -> a2.getAppointmentDate().compareTo(a1.getAppointmentDate()))
                            .limit(3)
                            .map(a -> String.format("- %s %s (%s) 寵物:%s 美容師:%s (狀態:%s)",
                                    a.getAppointmentDate(), a.getStartTime(), a.getMainService(),
                                    a.getPetName(), a.getGroomerName(), a.getAppointmentStatus()))
                            .collect(Collectors.joining("\n"));
                    contextBuilder.append("【您最近的美容預約紀錄 (僅本人可見)】:\n").append(apptInfo).append("\n\n");
                } else {
                    contextBuilder.append("【預約查詢結果】: 您目前沒有美容預約紀錄。\n\n");
                }
            } else if (memberId == null && containsAny(lowerMsg, APPOINTMENT_KEYWORDS)) {
                contextBuilder.append("【系統提示】: 使用者詢問預約紀錄，但未登入，請引導登入。\n\n");
            }

            // 3. 組合 System Prompt
            String systemPrompt = String.format("""
                    你是寵物電商『MaoMaoLand』的智能客服 AI 助理。請用繁體中文、親切可愛的語氣(🐶, 🐱)回答。

                    【回應策略】：
                    1. **自我介紹**：若問「你是誰」，請簡短自我介紹。若直接問業務，直接回答問題。
                    2. **排版規定**：
                       - 禁止使用 Markdown 表格。
                       - **通用卡片格式**：
                         (Emoji) **[名稱]**
                         💰 [價格/折扣]：[...]
                         ✨ [說明]：[...]
                         -------------------
                         (Emoji 參考: 美容✂️, 商品🐶, 優惠🎫, 美容師💇, 訂單📦, 預約📅)
                    3. **合併邏輯**：相同服務不同價格請合併顯示 (例如: $500 - $1200)。
                    4. **訂單/預約查詢**：若上方有提供資料，請整理給使用者；若無資料請誠實告知。

                    【網站基礎規範】：
                    1. 運費與免運：運費 $60，消費滿 $1,000 即享免運。
                    2. 會員幣：每筆訂單回饋 1%%，可無上限折抵。
                    3. 修改資料：請點擊右上方「會員中心圖示」。

                    %s

                    【回答守則】：
                    1. 優先根據上方資訊卡回答。
                    2. 若商品沒庫存/查無紀錄，請誠實告知。
                    3. 若使用者要求『真人客服』或無法解決，請回答『好的，已為您轉接真人客服，請稍候...』。
                    4. 通用寵物知識可直接回答。

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
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            // 9. 解析回傳結果
            JsonNode responseJson = objectMapper.readTree(response.getBody());

            // 成功取得回應，直接 return 跳出迴圈
            return responseJson.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

        } catch (HttpClientErrorException.TooManyRequests e) {
            // --- 針對 429 錯誤：不要重試，直接投降 ---
            // 原因：免費版限制每分鐘請求數，重試只會讓限制時間變長。
            System.err.println("API 429 限速刑求: " + e.getMessage());

            // 直接回傳備案訊息
            return "🐶 哎呀！現在諮詢的人數有點多，小幫手腦袋運轉過熱了💦\n" +
                    "請您稍等 1 分鐘後再試，或是輸入『真人客服』由專人為您服務喔！";

        } catch (Exception e) {
            // --- 其他錯誤不重試，直接報錯 ---
            e.printStackTrace();
            return "抱歉，AI 大腦運轉過熱中... (錯誤代碼: " + e.getClass().getSimpleName() + ")";
        }
    }

    // 輔助方法：檢查訊息是否包含任一關鍵字
    private boolean containsAny(String input, List<String> keywords) {
        for (String k : keywords) {
            if (input.contains(k.toLowerCase()))
                return true;
        }
        return false;
    }
}