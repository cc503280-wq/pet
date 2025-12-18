package com.pet.model.member;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "member_coupon_view")
public class CouponUsers {
	
	@Id @Column(name = "id")
	private int id;
	
	@Column(name = "member_id")
	private int memberId;
	
	@Column(name = "coupon_id")
	private int couponId;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "assigned_at")
	private Timestamp assignedAt;
	
	@Column(name = "used_at")
	private Timestamp usedAt;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "discount_type")
	private String discountType;
	
	@Column(name = "discount_value")
	private double discountValue;
	
	@Column(name = "min_purchase")
	private int minPurchase;
	
	@Column(name = "issue_start_at")
	private Date issueStartAt;
	
	@Column(name = "issue_end_at")
	private Date issueEndAt;
	
	@Column(name = "use_start_at")
	private Date useStartAt;
	
	@Column(name = "use_end_at")
	private Date useEndAt;
	
	@Column(name = "is_expired")
	private String isExpired;
	
	public CouponUsers() {
		super();
	}

	public CouponUsers(int id, int memberId, int couponId, String status, Timestamp assignedAt, Timestamp usedAt,
			String code, String discountType, double discountValue, int minPurchase, Date issueStartAt, Date issueEndAt,
			Date useStartAt, Date useEndAt, String isExpired) {
		super();
		this.id = id;
		this.memberId = memberId;
		this.couponId = couponId;
		this.status = status;
		this.assignedAt = assignedAt;
		this.usedAt = usedAt;
		this.code = code;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.minPurchase = minPurchase;
		this.issueStartAt = issueStartAt;
		this.issueEndAt = issueEndAt;
		this.useStartAt = useStartAt;
		this.useEndAt = useEndAt;
		this.isExpired = isExpired;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public int getCouponId() {
		return couponId;
	}

	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(Timestamp assignedAt) {
		this.assignedAt = assignedAt;
	}

	public Timestamp getUsedAt() {
		return usedAt;
	}

	public void setUsedAt(Timestamp usedAt) {
		this.usedAt = usedAt;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDiscountType() {
		return discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public double getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(double discountValue) {
		this.discountValue = discountValue;
	}

	public int getMinPurchase() {
		return minPurchase;
	}

	public void setMinPurchase(int minPurchase) {
		this.minPurchase = minPurchase;
	}

	public Date getIssueStartAt() {
		return issueStartAt;
	}

	public void setIssueStartAt(Date issueStartAt) {
		this.issueStartAt = issueStartAt;
	}

	public Date getIssueEndAt() {
		return issueEndAt;
	}

	public void setIssueEndAt(Date issueEndAt) {
		this.issueEndAt = issueEndAt;
	}

	public Date getUseStartAt() {
		return useStartAt;
	}

	public void setUseStartAt(Date useStartAt) {
		this.useStartAt = useStartAt;
	}

	public Date getUseEndAt() {
		return useEndAt;
	}

	public void setUseEndAt(Date useEndAt) {
		this.useEndAt = useEndAt;
	}

	public String getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(String isExpired) {
		this.isExpired = isExpired;
	}
	
	
	
	
	
}
