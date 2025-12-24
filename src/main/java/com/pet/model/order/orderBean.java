package com.pet.model.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "orders")
public class orderBean implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @Column(name = "order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderId;
	@Column(name ="member_id")
	private Integer memberId;
	@Column(name ="order_date")
	private Date orderDate;
	@Column(name ="status")
	private String status;
	@Column(name ="total_amount_undiscount")
	private BigDecimal totalAmountUndiscount;
	@Column(name ="coupon_id")
	private Integer couponId;
	@Column(name ="total_amount_discount")
	private BigDecimal totalAmountDiscount;
	@Column(name ="use_points")
	private Integer usePoints;
	@Column(name ="total_amount_discount_points")
	private BigDecimal totalAmountDiscountPoints;
	@Column(name ="get_points")
	private Integer getPoints;
	
	
	
	
	public orderBean(Integer orderId, Integer memberId, Date orderDate, String status, BigDecimal totalAmountUndiscount,
			Integer couponId, BigDecimal totalAmountDiscount, Integer usePoints, BigDecimal totalAmountDiscountPoints,
			Integer getPoints) {
		super();
		this.orderId = orderId;
		this.memberId = memberId;
		this.orderDate = orderDate;
		this.status = status;
		this.totalAmountUndiscount = totalAmountUndiscount;
		this.couponId = couponId;
		this.totalAmountDiscount = totalAmountDiscount;
		this.usePoints = usePoints;
		this.totalAmountDiscountPoints = totalAmountDiscountPoints;
		this.getPoints = getPoints;
	}
	
	
	public orderBean() {
		super();
	}


	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getTotalAmountUndiscount() {
		return totalAmountUndiscount;
	}
	public void setTotalAmountUndiscount(BigDecimal totalAmountUndiscount2) {
		this.totalAmountUndiscount = totalAmountUndiscount2;
	}
	public Integer getCouponId() {
		return couponId;
	}
	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}
	public BigDecimal getTotalAmountDiscount() {
		return totalAmountDiscount;
	}
	public void setTotalAmountDiscount(BigDecimal totalAmountDiscount) {
		this.totalAmountDiscount = totalAmountDiscount;
	}
	public Integer getUsePoints() {
		return usePoints;
	}
	public void setUsePoints(Integer usePoints) {
		this.usePoints = usePoints;
	}
	public BigDecimal getTotalAmountDiscountPoints() {
		return totalAmountDiscountPoints;
	}
	public void setTotalAmountDiscountPoints(BigDecimal totalAmountDiscountPoints) {
		this.totalAmountDiscountPoints = totalAmountDiscountPoints;
	}
	public Integer getGetPoints() {
		return getPoints;
	}
	public void setGetPoints(Integer getPoints) {
		this.getPoints = getPoints;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
