package com.pet.service.member;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dao.member.CouponRepository;
import com.pet.dao.member.CouponUsersRealRepository;
import com.pet.model.member.Coupon;
import com.pet.model.member.CouponUsers;
import com.pet.model.member.CouponUsersReal;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CouponUsersRealService {
	@Autowired
	private CouponUsersRealRepository curRepo;
	
	@Autowired
	private CouponRepository couponRepository;
	
	public Integer CouponUsersUpdate(Integer id,String status,LocalDate usedAt) {
		return curRepo.UpdateCouponUsersReal(id,status,usedAt);
	}
	
	public void assignWelcomeCoupon(Integer memberId) {
	    // 1. 先找到「新會員專屬優惠券」模板
	    Coupon welcomeCoupon = couponRepository.findByCode("WELCOME100")
	                                 .orElseThrow(() -> new RuntimeException("找不到新手優惠券"));

	    // 2. 如果模板裡的使用期限還沒設，這裡可以臨時設定（可選）
	    if (welcomeCoupon.getUseStartAt() == null) {
	        welcomeCoupon.setUseStartAt(LocalDate.now());
	    }
	    if (welcomeCoupon.getUseEndAt() == null) {
	        welcomeCoupon.setUseEndAt(LocalDate.now().plusMonths(1));
	    }

	    // 3. 建立 CouponUsersReal，只存會員領券紀錄
	    CouponUsersReal couponUser = CouponUsersReal.builder()
	        .memberId(memberId)
	        .couponId(welcomeCoupon.getCouponId())
	        .status("unused")   // 未使用
	        .build();

	    // 4. 存進 coupon_users 表
	    curRepo.save(couponUser);
	}
	//根據會員編號與優惠券編號查詢會員優惠券編號
	public void rollbackCouponStatus(Integer memberId, Integer couponId) {
	    // 1. 找出該會員「已使用」的該張優惠券紀錄
	    // 注意：查詢條件建議加上 status = 'used'，確保不會改錯
	    CouponUsersReal record = curRepo.findByCouponIdAndMemberId(couponId, memberId)
	        .orElseThrow(() -> new RuntimeException("找不到對應的優惠券使用紀錄"));

	    // 2. 執行回滾更新 (將狀態改回 unused)
	    // 這裡可以直接用你的 Repository 方法，或直接操作實體物件
	    record.setStatus("unused");
	    record.setUsedAt(null);
	    
	    curRepo.save(record); 
	    System.out.println("優惠券回滾成功，紀錄 ID: " + record.getId());
	}
}
