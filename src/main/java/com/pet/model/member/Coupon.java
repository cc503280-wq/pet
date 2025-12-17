package com.pet.model.member;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class Coupon {
	
	private int couponId;
	private String code;
	private String discountType;
	private double discountValue;
	private int isLimited;
	private Integer totalAmount; //容許null
	private Integer issuedAmount; //容許null
	private Date issueStartAt;
	private Date issueEndAt;
	private Date useStartAt;
	private Date useEndAt;
	private int minPurchase;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private String status;
	
	public Coupon() {
		super();
	}

	public Coupon(int couponId, String code, String discountType, double discountValue, int isLimited, Integer totalAmount,
			Integer issuedAmount, Date issueStartAt, Date issueEndAt, Date useStartAt, Date useEndAt, int minPurchase,
			Timestamp createdAt, Timestamp updatedAt, String status) {
		super();
		this.couponId = couponId;
		this.code = code;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.isLimited = isLimited;
		this.totalAmount = totalAmount;
		this.issuedAmount = issuedAmount;
		this.issueStartAt = issueStartAt;
		this.issueEndAt = issueEndAt;
		this.useStartAt = useStartAt;
		this.useEndAt = useEndAt;
		this.minPurchase = minPurchase;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.status = status;
	}
	
	public Coupon(String code, String discountType, double discountValue, int isLimited, Integer totalAmount,
			Integer issuedAmount, Date issueStartAt, Date issueEndAt, Date useStartAt, Date useEndAt, int minPurchase) {
		super();
		this.code = code;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.isLimited = isLimited;
		this.totalAmount = totalAmount;
		this.issuedAmount = issuedAmount;
		this.issueStartAt = issueStartAt;
		this.issueEndAt = issueEndAt;
		this.useStartAt = useStartAt;
		this.useEndAt = useEndAt;
		this.minPurchase = minPurchase;
	}

	public Coupon(int couponId, String code, String discountType, double discountValue, int isLimited,
			Integer totalAmount, Integer issuedAmount, Date issueStartAt, Date issueEndAt, Date useStartAt,
			Date useEndAt, int minPurchase) {
		super();
		this.couponId = couponId;
		this.code = code;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.isLimited = isLimited;
		this.totalAmount = totalAmount;
		this.issuedAmount = issuedAmount;
		this.issueStartAt = issueStartAt;
		this.issueEndAt = issueEndAt;
		this.useStartAt = useStartAt;
		this.useEndAt = useEndAt;
		this.minPurchase = minPurchase;
	}

	public int getCouponId() {
		return couponId;
	}

	public void setCouponId(int couponId) {
		this.couponId = couponId;
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

	public int getIsLimited() {
		return isLimited;
	}

	public void setIsLimited(int isLimited) {
		this.isLimited = isLimited;
	}

	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getIssuedAmount() {
		return issuedAmount;
	}

	public void setIssuedAmount(Integer issuedAmount) {
		this.issuedAmount = issuedAmount;
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

	public int getMinPurchase() {
		return minPurchase;
	}

	public void setMinPurchase(int minPurchase) {
		this.minPurchase = minPurchase;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
