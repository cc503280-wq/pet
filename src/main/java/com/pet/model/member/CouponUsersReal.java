package com.pet.model.member;

import java.time.LocalDate;


import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity @Table(name = "couponUsers")
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class CouponUsersReal {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Integer memberId;
	private Integer couponId;
	@NonNull
	private String status;
	private LocalDate assignedAt;
	@NonNull
	private LocalDate usedAt;
}
