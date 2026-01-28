package com.pet.controller.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dto.member.CouponDTO;
import com.pet.dto.order.OrderCheckOutDTO;
import com.pet.model.order.Order;
import com.pet.service.member.CouponUsersService;
import com.pet.service.order.OrderService;
import com.pet.util.LoginUser;

@RestController
@RequestMapping("/shop/checkout")
public class CheckOut {
	@Autowired
	private CouponUsersService couponUsersService;
	@Autowired
	private OrderService orderService;

	@GetMapping("/coupon")
	public List<CouponDTO> checkCouponUsers(@LoginUser Integer userId) {
		return couponUsersService.getCouponDTO(userId);
	}
	@PostMapping("/insert")
	public ResponseEntity<?> insertOrders(@RequestBody OrderCheckOutDTO orderRequest) {
		// 1. 驗證資料是否正確接收
        System.out.println("收到訂單請求，收件人：" + orderRequest.getReceiverName());
        System.out.println("使用點數：" + orderRequest.getPointsUsed());

        // 2. 呼叫 Service 執行建立訂單邏輯
        Order order = orderService.userOrder(orderRequest);

        // 3. 回傳成功訊息 (或是回傳剛建立的訂單 ID)
        return ResponseEntity.ok().body("訂單建立成功");
	}
	
}
