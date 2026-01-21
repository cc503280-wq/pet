package com.pet.dao.member;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	@Query("""
			SELECT c
			FROM CouponUsers c
			WHERE c.memberId = :memberId
			  AND c.status = 'unused'
			  AND c.isExpired = 'N'
			  AND :today BETWEEN c.useStartAt AND COALESCE(c.useEndAt, :today)
			  AND :orderTotal >= c.minPurchase
			""")
	List<CouponUsers> findUsableCoupons(@Param("memberId") Integer memberId, @Param("today") LocalDate today,
			@Param("orderTotal") BigDecimal orderTotal);
}
