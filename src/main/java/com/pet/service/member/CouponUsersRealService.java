package com.pet.service.member;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dao.member.CouponUsersRealRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CouponUsersRealService {
	@Autowired
	private CouponUsersRealRepository curRepo;
	
	public Integer CouponUsersUpdate(Integer id,String status,LocalDate usedAt) {
		return curRepo.UpdateCouponUsersReal(id,status,usedAt);
	}
}
