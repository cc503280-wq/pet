package com.pet.controller.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.member.Coupon;
import com.pet.service.member.CouponService;
import com.pet.util.LoginUser;

@RestController
@RequestMapping("/shop/coupons")
public class ShopCouponController {

	@Autowired
    private CouponService couponService;

	//懸浮球獲取所有「可領取」的優惠券清單
    @GetMapping("/active")
    public ResponseEntity<List<Coupon>> getActiveCoupons() {
        // 直接利用你原本 Service 就有的 getCouponsByStatus
        List<Coupon> activeList = couponService.getCouponsByStatus("active");
        return ResponseEntity.ok(activeList);
    }

    //點擊領取按鈕
    @PostMapping("/claim/{couponId}")
    public ResponseEntity<?> claim(@LoginUser Integer userId, @PathVariable Integer couponId) {
        // 1. 檢查登入狀態
        if (userId == null) {
            return ResponseEntity.status(401).body("請先登入後再領取優惠券");
        }

        try {
            // 2. 執行領取邏輯
            couponService.claimCoupon(userId, couponId);
            return ResponseEntity.ok("領取成功！");
            
        } catch (RuntimeException e) {
            // 3. 捕捉業務上的錯誤 (例如：已領過、領完、過期)，回傳 400 給前端 Swal 顯示
            return ResponseEntity.badRequest().body(e.getMessage());
            
        } catch (Exception e) {
            // 4. 其他不可預期的錯誤
            return ResponseEntity.internalServerError().body("伺服器繁忙，請稍後再試");
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableCoupons() {
        return ResponseEntity.ok(couponService.getAvailableCoupons());
    }
}
