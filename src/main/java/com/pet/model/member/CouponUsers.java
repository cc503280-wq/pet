package com.pet.model.member;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "member_coupon_view")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUsers {
	
	@Id @Column(name = "id")
	private Integer id;
	
	@Column(name = "member_id")
	private Integer memberId;
	
	@Column(name = "coupon_id")
	private Integer couponId;
	
	private String status;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "assigned_at")
	private LocalDateTime assignedAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "used_at")
	private LocalDateTime usedAt;
	
	private String code;
	
	@Column(name = "discount_type")
	private String discountType;
	
	@Column(name = "discount_value")
	private Double discountValue;
	
	@Column(name = "min_purchase")
	private Integer minPurchase;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(name = "issue_start_at")
	private LocalDate issueStartAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(name = "issue_end_at")
	private LocalDate issueEndAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(name = "use_start_at")
	private LocalDate useStartAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(name = "use_end_at")
	private LocalDate useEndAt;
	
	@Column(name = "is_expired")
	private String isExpired;
	
}
