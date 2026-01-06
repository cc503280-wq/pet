package com.pet.controller.member;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.member.Coupon;
import com.pet.service.member.CouponService;

@RestController
@RequestMapping("/coupon")
public class CouponController {

	@Autowired
    private CouponService couponService;

    // 1. 查詢全部或依狀態篩選 (GET /coupons?status=active)
    @GetMapping
    public List<Coupon> list(@RequestParam(required = false, defaultValue = "all") String status) {
        if ("all".equals(status)) {
            return couponService.getAllCoupons();
        }
        return couponService.getCouponsByStatus(status);
    }

    // 2. 依 ID 查詢單筆 (GET /coupons/5)
    @GetMapping("/{id}")
    public ResponseEntity<Coupon> queryById(@PathVariable Integer id) {
        Coupon coupon = couponService.getCouponById(id);
        if (coupon != null) {
            return ResponseEntity.ok(coupon);
        }
        return ResponseEntity.notFound().build(); // 回傳 404
    }

    // 3. 依發放期間查詢 (GET /coupons/search/issue?start=2024-01-01&end=2024-01-31)
    @GetMapping("/search/issue")
    public List<Coupon> queryByIssueRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return couponService.getCouponsByIssueRange(start, end);
    }

    // 4. 依使用期間查詢 (GET /coupons/search/use?start=2024-01-01&end=2024-01-31)
    @GetMapping("/search/use")
    public List<Coupon> queryByUseRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return couponService.getCouponsByUseRange(start, end);
    }

    // 5. 新增優惠券 (POST /coupons)
    @PostMapping
    public ResponseEntity<Coupon> create(@RequestBody Coupon coupon) {
        Coupon savedCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.ok(savedCoupon);
    }

    // 6. 修改優惠券 (PUT /coupons/5)
    @PutMapping("/{id}")
    public ResponseEntity<Coupon> update(@PathVariable Integer id, @RequestBody Coupon coupon) {
        coupon.setCouponId(id); // 確保 ID 是路徑上的 ID
        Coupon updatedCoupon = couponService.updateCoupon(coupon);
        if (updatedCoupon != null) {
            return ResponseEntity.ok(updatedCoupon);
        }
        return ResponseEntity.notFound().build();
    }

    // 7. 切換狀態 (PATCH /coupons/5/toggle)
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggleStatus(@PathVariable Integer id) {
        boolean success = couponService.toggleStatus(id);
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "狀態已切換");
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        return ResponseEntity.status(404).body(response);
    }

}
