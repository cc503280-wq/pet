package com.pet.dao.member;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.member.CouponUsersReal;

public interface CouponUsersRealRepository extends JpaRepository<CouponUsersReal, Integer> {
	
	@Query("""
			UPDATE CouponUsersReal cu
			SET 
				cu.status =:status,
				cu.usedAt = :today
			where 
				cu.id = :id
			AND cu.status = 'unused'
				
			""")
	@Modifying
	Integer UpdateCouponUsersReal(@Param("id") Integer id,@Param("status") String status,@Param("today") LocalDate today);
}
