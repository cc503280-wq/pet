package com.pet.dao.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pet.model.member.CouponUserRecord;

@Repository
public interface CouponUserRecordRepository extends JpaRepository<CouponUserRecord, Integer> {

	// 用於檢查該會員是否已經領過該張券
    boolean existsByMemberIdAndCouponId(Integer memberId, Integer couponId);
}
