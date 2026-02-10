package com.pet.service.order;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.pet.dao.order.ShipmentRepository;
import com.pet.model.order.Shipment;

@Service
public class EcpayService {

	private final ShipmentRepository shipmentRepository;
	@Autowired
	private OrderService oService;

	// === 綠界測試環境參數 (Stage) ===
	// 正式上線時請切換為正式環境的 Key/IV 與 URL
	private final String MERCHANT_ID = "3002607";
	private final String HASH_KEY = "pwFHCqoQZGmho4w6";
	private final String HASH_IV = "EkRm7iFT261dpevs";
	private final String ACTION_URL = "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5";

	// 修改成後端位置
	private final String BASE_URL = "https://unchid-technologically-pok.ngrok-free.dev";

	// 回傳網址 (請改為您的實際網址，本地開發需用 ngrok)
	// ReturnURL: 綠界背景呼叫，告知付款結果
	private final String RETURN_URL = BASE_URL + "/shop/checkout/callback";
	// ClientBackURL: 使用者付款完成後，點擊按鈕返回的網址
	private final String CLIENT_BACK_URL = "http://localhost:5173";

	// 綠界物流地圖 API (測試環境)
	private final String LOGISTICS_ACTION_URL = "https://logistics-stage.ecpay.com.tw/Express/map";

	// 地圖選完後，綠界 POST 回來的後端網址 (必須是 ngrok 外網)
	private final String MAP_CALLBACK_URL = BASE_URL + "/shop/checkout/map_callback";

	EcpayService(ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}

	/**
	 * 產生綠界金流表單 HTML
	 * 
	 * @param orderId     訂單編號
	 * @param totalAmount 總金額
	 * @param itemName    商品名稱 (例如: "寵物飼料 x1, 玩具 x2")
	 * @return HTML Form 字串
	 */
	public String createEcpayForm(String orderId, BigDecimal totalAmount, String itemName, String saveId) {
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
			html.append("<input type='hidden' name='").append(entry.getKey()).append("' value='")
					.append(entry.getValue()).append("'/>");
		}

		html.append("</form>");
		// 自動送出表單的 Script
		html.append("<script>document.getElementById('ecpay-form').submit();</script>");
		html.append("</body></html>");

		return html.toString();
	}

	/**
	 * 計算 CheckMacValue 邏輯：排序 -> 串接 Key/IV -> URL Encode -> 轉小寫 -> Replace 特殊字元 ->
	 * SHA256 -> 轉大寫
	 */
	private String generateCheckMacValue(Map<String, String> params) {
		try {
			// 1. 參數串接 (Key=Value&Key=Value...)
			String queryString = params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
					.collect(Collectors.joining("&"));

			// 2. 頭尾加上 HashKey 與 HashIV
			String rawString = "HashKey=" + HASH_KEY + "&" + queryString + "&HashIV=" + HASH_IV;

			// 3. URL Encode (Java 預設會把空格轉為 +，需注意)
			String urlEncoded = URLEncoder.encode(rawString, StandardCharsets.UTF_8.name()).toLowerCase(); // 綠界規定轉小寫

			// 4. 處理 Java URLEncoder 與 .NET 的差異 (綠界規定要替換這些字元)
			urlEncoded = urlEncoded.replace("%2d", "-").replace("%5f", "_").replace("%2e", ".").replace("%21", "!")
					.replace("%2a", "*").replace("%28", "(").replace("%29", ")");

			// 5. SHA256 加密
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(urlEncoded.getBytes(StandardCharsets.UTF_8));

			// 6. 轉成大寫的 Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
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
			Integer saveId = Integer.valueOf(params.get("CustomField1"));
			// TODO: 請在此處呼叫您的 Repository 更新資料庫訂單狀態
			oService.updateOrderStatus(saveId, "付款完成");

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
			html.append("<input type='hidden' name='").append(entry.getKey()).append("' value='")
					.append(entry.getValue()).append("'/>");
		}
		html.append("</form>");
		html.append("<script>document.getElementById('ecpay-map-form').submit();</script>");
		html.append("</body></html>");

		return html.toString();
	}

	// 轉換前端的物流名稱為綠界代碼
	private String convertToLogisticsSubType(String method) {
		if (method == null)
			return "UNIMART";
		switch (method) {
		case "7-11":
			return "UNIMART"; // 統一超商
		case "fami":
			return "FAMI"; // 全家
		case "hi-life":
			return "HILIFE"; // 萊爾富
		default:
			return "UNIMART";
		}
	}

	// === 綠界 C2C 物流測試環境參數 ===
	private final String MERCHANT_ID_shipment = "2000933";
	private final String HASH_KEY_shipment = "XBERn1YOvpM9nfZc";
	private final String HASH_IV_shipment = "h1ONHk4P4yqbl5LK";

	// 物流 API 網址
	private final String LOGISTICS_CREATE_URL = "https://logistics-stage.ecpay.com.tw/Express/Create";
	private final String SERVER_REPLY_URL = BASE_URL + "/shipment/callback"; // 接收物流狀態通知

	/**
	 * 建立綠界物流訂單 (C2C 幕後呼叫)
	 * 
	 * @param shipment 您的 Shipment Entity
	 * @return 綠界回傳的結果字串
	 */
	public String createLogisticsOrder(Shipment shipment) {
		Map<String, String> params = new TreeMap<>();

		// 1. 設定必要參數 (維持原樣)
		params.put("MerchantID", MERCHANT_ID_shipment);
		// 建議使用訂單編號加上時間戳，確保物流單號在綠界系統中不重複
		params.put("MerchantTradeNo", "T" + shipment.getShipmentId() + System.currentTimeMillis() / 1000);
		params.put("MerchantTradeDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
		params.put("LogisticsType", "CVS");
		params.put("LogisticsSubType", convertToC2CSubType(shipment.getShippingMethod()));
		params.put("GoodsAmount", String.valueOf(shipment.getOrder().getTotalAmountUndiscount().intValue()));
		params.put("CollectionAmount", "0");
		params.put("IsCollection", "N");
		params.put("GoodsName", "PetProduct");

		params.put("SenderName", "maomaoland");
		params.put("SenderCellPhone", "0912345678");

		params.put("ReceiverName", shipment.getRecipientName());
		params.put("ReceiverCellPhone", shipment.getRecipientPhone());
		params.put("ReceiverStoreID", shipment.getStoreId());
		params.put("ServerReplyURL", SERVER_REPLY_URL);

		// 2. 產生檢查碼
		String checkMacValue = generateLogisticsCheckMacValue(params);
		params.put("CheckMacValue", checkMacValue);

		// 3. 發送請求
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		params.forEach(requestBody::add);

		try {
			// 同步取得綠界回傳字串
			String response = restTemplate.postForObject(LOGISTICS_CREATE_URL, requestBody, String.class);

			// 4. 解析並更新 Shipment 資料

			if (response != null && response.startsWith("1|")) {
				// 1. 拆分字串，只拿 "|" 後面的參數部分
				String resultData = response.substring(2);
				Map<String, String> resMap = parseQueryString(resultData);

				// 2. 從 Map 中取出綠界給的編號
				String logisticsId = resMap.get("AllPayLogisticsID");
				String paymentNo = resMap.get("CVSPaymentNo"); // 這就是「寄貨編號」
				String validationNo = resMap.get("CVSValidationNo"); // 7-11 必備驗證碼

				// 3. 塞回實體並存進資料庫
				shipment.setLogisticsId(logisticsId);
				shipment.setDeliverySn(paymentNo);
				// 如果你有存驗證碼的欄位：shipment.setCvsValidationNo(validationNo);
				shipment.setStatus("待出貨");

				shipmentRepository.save(shipment); // 存檔，這樣之後才查得到

				// 4. 回傳給前端看的訊息（建議轉成 JSON 或格式化字串）
				return "成功！物流單號：" + logisticsId + "，寄件編號：" + paymentNo;
			} else {
				return "失敗：" + response;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "0|ConnectionError: " + e.getMessage();
		}
	}

	/**
	 * 解析綠界回傳結果並更新 Shipment Entity
	 */
	private void parseLogisticsResponse(String response, Shipment shipment) {
		if (response != null && response.startsWith("1|")) {
			String data = response.substring(2);
			Map<String, String> resMap = parseQueryString(data);

			// 更新您的 Entity 欄位
			shipment.setLogisticsId(resMap.get("AllPayLogisticsID")); // 綠界物流編號
			shipment.setDeliverySn(resMap.get("CVSPaymentNo")); // 寄貨編號
			shipment.setStatus("待出貨");

			// 如果是 7-11 C2C，還會有 CVSValidationNo (驗證碼)
			// 建議在 Shipment Entity 增加一個 validationNo 欄位存儲，因為 7-11 寄貨需兩者組合
		}
	}

	/**
	 * 計算物流專用 CheckMacValue (MD5 版本)
	 */
	private String generateLogisticsCheckMacValue(Map<String, String> params) {
		try {
			String queryString = params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
					.collect(Collectors.joining("&"));

			String rawString = "HashKey=" + HASH_KEY_shipment + "&" + queryString + "&HashIV=" + HASH_IV_shipment;
			System.out.println(rawString);

			// URL Encode 處理
			String urlEncoded = URLEncoder.encode(rawString, StandardCharsets.UTF_8.name()).toLowerCase();

			// 綠界特有的符號替換 (與金流相同)
			urlEncoded = urlEncoded.replace("%2d", "-").replace("%5f", "_").replace("%2e", ".").replace("%21", "!")
					.replace("%2a", "*").replace("%28", "(").replace("%29", ")");
			// 額外處理空格轉為 + 號 (物流 API 若 PHP 習慣可能會用 +)
			urlEncoded = urlEncoded.replace("%20", "+");

			// MD5 加密
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(urlEncoded.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : array) {
				sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException("MD5 Error", e);
		}
	}

	private String convertToC2CSubType(String method) {
		switch (method) {
		case "7-11":
			return "UNIMARTC2C";
		case "fami":
			return "FAMIC2C";
		case "hi-life":
			return "HILIFEC2C";
		case "ok":
			return "OKMARTC2C";
		default:
			return "UNIMARTC2C";
		}
	}

	private Map<String, String> parseQueryString(String query) {
		Map<String, String> map = new TreeMap<>();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			if (idx > 0) {
				map.put(pair.substring(0, idx), pair.substring(idx + 1));
			}
		}
		return map;
	}

	/**
	 * 處理綠界物流狀態回調 (ServerReplyURL) 驗證通過後，根據 RtnCode 更新貨態
	 */
	public String handleLogisticsCallback(Map<String, String> params) {
		System.out.println("收到綠界物流狀態回調: " + params);

		// 1. 驗證 CheckMacValue (物流 C2C 使用 MD5)
		Map<String, String> verifyParams = new TreeMap<>(params);
		verifyParams.remove("CheckMacValue");
		String calculatedMac = generateLogisticsCheckMacValue(verifyParams);

		if (!calculatedMac.equals(params.get("CheckMacValue"))) {
			System.out.println("⚠️ 物流回調驗證失敗：CheckMacValue 不符");
			return "0|CheckMacValue Error";
		}

		// 2. 獲取核心資訊
		String allPayLogisticsID = params.get("AllPayLogisticsID"); // 綠界物流編號
		String rtnCode = params.get("RtnCode"); // 狀態代碼
		String rtnMsg = params.get("RtnMsg"); // 狀態說明

		// 3. 更新資料庫狀態
		// 這裡建議透過 shipmentRepository 找到對應的訂單並更新
		// 常見 RtnCode 參考：
		// 300: 訂單處理中 (已建立訂單)
		// 2030: 商品已送達門市 (請買家取貨)
		// 2061: 買家已取貨完成
		// 2063: 買家逾期未取 (退回原寄件門市)
		// 30033: 商品已退回到原寄件門市 (賣家需去領回)

		updateShipmentStatusByLogisticsId(allPayLogisticsID, rtnCode, rtnMsg);

		return "1|OK"; // 必須回傳 1|OK 給綠界
	}

	// 內部更新邏輯範例
	private void updateShipmentStatusByLogisticsId(String logisticsId, String code, String msg) {
		
		Shipment shipment = shipmentRepository.findByLogisticsId(logisticsId).orElseThrow();
		
		shipment.setStatus(msg);
		if ("2061".equals(code)) shipment.setDeliveredAt(LocalDateTime.now());
		shipmentRepository.save(shipment);

		System.out.println("✅ 物流單 " + logisticsId + " 已更新狀態為: " + msg);
	}
}
