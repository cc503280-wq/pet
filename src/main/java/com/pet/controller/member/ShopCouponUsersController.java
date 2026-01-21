package com.pet.controller.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.member.CouponUsers;
import com.pet.service.member.CouponUsersService;
import com.pet.util.LoginUser;

@RestController
@RequestMapping("/shop/couponusers")
public class ShopCouponUsersController {

    @Autowired
    private CouponUsersService couponUsersService; 

    //取得目前登入者的優惠券清單
    @GetMapping
    public ResponseEntity<?> getMyCoupons(@LoginUser Integer userId) {
        
        if (userId == null) {
            return ResponseEntity.status(401).body("未登入");
        }

        List<CouponUsers> myCoupons = couponUsersService.getCouponUsersByMemberId(userId);

        return ResponseEntity.ok(myCoupons);
    }
}