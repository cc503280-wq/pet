package com.pet.controller.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dto.member.CouponDTO;
import com.pet.service.member.CouponUsersService;
import com.pet.util.LoginUser;

@RestController
@RequestMapping("/shop/checkout")
public class CheckOut {
	@Autowired
	private CouponUsersService couponUsersService;

	@GetMapping("/coupon")
	public List<CouponDTO> checkCouponUsers(@LoginUser Integer userId) {
		return couponUsersService.getCouponDTO(userId);
	}
}
