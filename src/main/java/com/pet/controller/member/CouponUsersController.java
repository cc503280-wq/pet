package com.pet.controller.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.member.CouponUsers;
import com.pet.service.member.CouponUsersService;

@RestController
@RequestMapping("/couponUsers")
public class CouponUsersController {

	@Autowired
	private CouponUsersService couponUsersService;
	
	// 查詢全部
	@GetMapping
    public List<CouponUsers> listAll() {
        return couponUsersService.getAllCouponUsers();
    }

    // 依 couponId 查詢
	@GetMapping("/coupon/{couponId}")
    public List<CouponUsers> queryByCouponId(@PathVariable Integer couponId) {
        return couponUsersService.getCouponUsersByCouponId(couponId);
    }

    // 依 memberId 查詢
	@GetMapping("/member/{memberId}")
    public List<CouponUsers> queryByMemberId(@PathVariable Integer memberId) {
        return couponUsersService.getCouponUsersByMemberId(memberId);
    }
}
