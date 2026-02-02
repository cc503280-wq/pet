package com.pet.controller.order;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dto.member.CouponDTO;
import com.pet.dto.order.MapRequest;
import com.pet.dto.order.OrderCheckOutDTO;
import com.pet.model.order.Order;
import com.pet.service.member.CouponUsersService;
import com.pet.service.order.EcpayService;
import com.pet.service.order.OrderService;
import com.pet.util.LoginUser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/shop/checkout")
public class CheckOut {
	@Autowired
	private CouponUsersService couponUsersService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private EcpayService ecpayService;
	private final String FRONTEND_URL = "http://localhost:5173/#/checkout";

	@GetMapping("/coupon")
	public List<CouponDTO> checkCouponUsers(@LoginUser Integer userId) {
		return couponUsersService.getCouponDTO(userId);
	}

	@PostMapping("/insert")
	public ResponseEntity<?> insertOrders(@RequestBody OrderCheckOutDTO orderRequest) {
		

		// 2. 呼叫 Service 執行建立訂單邏輯
		Order order = orderService.userOrder(orderRequest);

		// 3.判斷付款方式
		if ("ECPay".equals(orderRequest.getPaymentMethod())) {
			String timestamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
			// 產生綠界金流的 HTML 表單字串
			String checkOutId = order.getOrderId()+"T" + timestamp ;
			String ecpayHtml = ecpayService.createEcpayForm(checkOutId, order.getTotalAmountDiscountPoints(), "寵物商城商品", // 或是從
																														// request
																														// 組合商品名稱
					String.valueOf(order.getOrderId()));
			return ResponseEntity.ok(ecpayHtml);
		} else {
			// 貨到付款或其他方式，直接回傳成功訊息
			return ResponseEntity.ok("Order Created Successfully");
		}

	}

	@PostMapping("/callback")
	public ResponseEntity<String> ecpayCallback(@RequestParam Map<String, String> params) {

		// 1. 呼叫 Service 處理邏輯 (驗證 MacValue、更新訂單狀態)
		// 假設您的 service 方法回傳的是 "1|OK" 或 "0|Error"
		String result = ecpayService.handleEcpayCallback(params);

		// 2. 明確回傳純文字格式
		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN) // 關鍵：強制指定為 text/plain
				.body(result);
	}

	/**
	 * 1. 前端呼叫此 API 取得綠界地圖表單
	 */
	@PostMapping("/map")
	public ResponseEntity<String> getMap(@RequestBody MapRequest request) {
		String html = ecpayService.createLogisticsForm(request.getShippingMethod());
		return ResponseEntity.ok(html);
	}

	/**
	 * 2. 綠界選完門市後，會 POST 到這裡 我們將參數轉為 Query String，然後 Redirect 回前端
	 */
	@PostMapping("/map_callback")
	public void mapCallback(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {

		// 取出綠界回傳的門市資訊
		String storeName = params.getOrDefault("CVSStoreName", "");
		String address = params.getOrDefault("CVSAddress", "");
		String storeId = params.getOrDefault("CVSStoreID", "");
		String subType = params.getOrDefault("LogisticsSubType", "");

		// 進行 URL Encode 防止中文亂碼
		String query = String.format("?CVSStoreName=%s&CVSAddress=%s&CVSStoreID=%s&LogisticsSubType=%s",
				URLEncoder.encode(storeName, StandardCharsets.UTF_8),
				URLEncoder.encode(address, StandardCharsets.UTF_8), URLEncoder.encode(storeId, StandardCharsets.UTF_8),
				URLEncoder.encode(subType, StandardCharsets.UTF_8));

		// 302 轉址回前端頁面
		response.sendRedirect(FRONTEND_URL + query);
	}

}
