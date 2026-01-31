package com.pet.dto.member;

import com.pet.model.member.Coupon;
import com.pet.model.member.CouponUsers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDTO {
	private Coupon coupon;
	private CouponUsers couponUsers;
}
