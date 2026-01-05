package com.pet.dao.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pet.model.member.CouponUsers;

@Repository
public interface CouponUsersRepository extends JpaRepository<CouponUsers, Integer> {
	
	// 查詢全部
    List<CouponUsers> findAllByOrderByIdAsc();

    // 依 couponId 查詢
    List<CouponUsers> findByCouponIdOrderByIdAsc(Integer couponId);

    // 依 memberId 查詢
    List<CouponUsers> findByMemberIdOrderByIdAsc(Integer memberId);
}
