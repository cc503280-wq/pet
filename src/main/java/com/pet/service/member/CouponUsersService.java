package com.pet.service.member;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dao.member.CouponRepository;
import com.pet.dao.member.CouponUsersRepository;
import com.pet.dto.member.CouponDTO;
import com.pet.model.member.Coupon;
import com.pet.model.member.CouponUsers;

@Service
public class CouponUsersService {
	
	@Autowired
	private CouponUsersRepository couponUsersRepository;
	
	@Autowired
	private CouponRepository couponRepository;
	
	//查詢全部
	public List<CouponUsers> getAllCouponUsers(){
		return couponUsersRepository.findAllByOrderByIdAsc();
	}
	
	//依couponId查詢
	public List<CouponUsers> getCouponUsersByCouponId(Integer couponId){
		return couponUsersRepository.findByCouponIdOrderByIdAsc(couponId);
	}
	
	//依memberId查詢
	public List<CouponUsers> getCouponUsersByMemberId(Integer memberId){
		return couponUsersRepository.findByMemberIdOrderByIdAsc(memberId);
	}
	
	public List<CouponUsers> getOrderCouponUsers(Integer memberId,BigDecimal totalPrice){
		return couponUsersRepository.findUsableCoupons(memberId,LocalDate.now(),totalPrice);
	}
	
	public List<CouponDTO> getCouponDTO(Integer memberId) {
		List<CouponDTO> list = new ArrayList<>();
	    
	    // 1. 確保這裡抓到的是 member_id = 1 的所有領用紀錄
	    List<CouponUsers> couponUserList = couponUsersRepository.findByMemberIdOrderByIdAsc(memberId);
	    
	    for (CouponUsers cu : couponUserList) {
	        // 2. 根據資料表內容，狀態必須是 "N" 且尚未被使用 ("unused")
	        if ("N".equals(cu.getIsExpired()) && "unused".equals(cu.getStatus())) {
	            
	            // 3. 抓取母檔資訊
	            couponRepository.findById(cu.getCouponId()).ifPresent(c -> {
	                // 4. 確認母檔本身還是 active 狀態
	                if ("active".equals(c.getStatus())) {
	                    CouponDTO dto = new CouponDTO();
	                    dto.setCouponUsers(cu); // 這裡面有 code, used_at...
	                    dto.setCoupon(c);       // 這裡面有 discount_value, coupon_name...
	                    list.add(dto);
	                }
	            });
	        }
	    }
		return list;
	}
	
}
