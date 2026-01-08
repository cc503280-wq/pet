package com.pet.dao.member;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pet.model.member.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {

	// 查詢全部 
    List<Coupon> findAllByOrderByCouponIdAsc();

    // 查詢啟用中
    List<Coupon> findByStatusOrderByCouponIdAsc(String status);

    // 依發放期間查詢
    @Query("SELECT c FROM Coupon c WHERE c.issueStartAt <= :end AND c.issueEndAt >= :start")
    List<Coupon> findByIssueRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // 依使用期間查詢
    @Query("SELECT c FROM Coupon c WHERE c.useStartAt <= :end AND c.useEndAt >= :start")
    List<Coupon> findByUseRange(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
