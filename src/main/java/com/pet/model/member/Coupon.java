package com.pet.model.member;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
	
	@Id @Column(name = "coupon_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer couponId;
	
	private String code;
	
	@Column(name = "discount_type")
	private String discountType;
	
	@Column(name = "discount_value")
	private Double discountValue;
	
	@Column(name = "is_limited")
	private Integer isLimited;
	
	@Column(name = "total_amount")
	private Integer totalAmount; 
	
	@Column(name = "issued_amount")
	private Integer issuedAmount; 
	
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
	
	@Column(name = "min_purchase")
	private Integer minPurchase;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	private String status;
	
	//更新前
	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;

		// 預設狀態
		if (this.status == null) {
			this.status = "active";
		}
		
		// 預設發放數量 (若為 null 則給 0)
		if (this.issuedAmount == null) {
			this.issuedAmount = 0;
		}
	}

	//修改前
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
	
}
