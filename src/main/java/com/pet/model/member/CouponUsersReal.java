package com.pet.model.member;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
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

@Entity
@Table(name = "coupon_users")
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class CouponUsersReal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "member_id")
	private Integer memberId;

	@Column(name = "coupon_id")
	private Integer couponId;

	@NonNull
	private String status;

	@Column(name = "assigned_at")
	private LocalDate assignedAt;

	@NonNull
	@Column(name = "used_at")
	private LocalDate usedAt;
}
