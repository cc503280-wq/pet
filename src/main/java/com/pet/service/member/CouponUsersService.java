package com.pet.service.member;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dao.member.CouponUsersRepository;
import com.pet.model.member.CouponUsers;

@Service
public class CouponUsersService {
	
	@Autowired
	private CouponUsersRepository couponUsersRepository;
	
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
	
}
