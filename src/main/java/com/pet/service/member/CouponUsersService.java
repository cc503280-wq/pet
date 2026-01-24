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
		List<CouponDTO> list = new ArrayList<CouponDTO>();
		List<CouponUsers> couponUserList= getCouponUsersByCouponId(memberId);
		for (CouponUsers couponUsers : couponUserList) {
			if ("Y".equals(couponUsers.getIsExpired())) {
				CouponDTO couponDTO = new CouponDTO();
				Coupon coupon =couponRepository.getById(couponUsers.getCouponId());
				if ("active".equals(coupon.getStatus())) {
					couponDTO.setCouponUsers(couponUsers);
					couponDTO.setCoupon(coupon);
					list.add(couponDTO);
				}
			}
		}
		return list;
	}
	
}
