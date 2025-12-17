package com.pet.model.order;

import java.io.Serializable;
import java.util.Date;

public class orderBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer orderId;
	private Integer memberId;
	private Date orderDate;
	private String status;
	private Double totalAmountUndiscount;
	private Integer coupon_id;
	private Double totalAmountDiscount;
	private Integer usePoints;
	private Double totalAmountDiscountPoints;
	private Integer getPoints;
	
	
	
	
	public orderBean(Integer orderId, Integer memberId, Date orderDate, String status, Double totalAmountUndiscount,
			Integer coupon_id, Double totalAmountDiscount, Integer usePoints, Double totalAmountDiscountPoints,
			Integer getPoints) {
		super();
		this.orderId = orderId;
		this.memberId = memberId;
		this.orderDate = orderDate;
		this.status = status;
		this.totalAmountUndiscount = totalAmountUndiscount;
		this.coupon_id = coupon_id;
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
	public Double getTotalAmountUndiscount() {
		return totalAmountUndiscount;
	}
	public void setTotalAmountUndiscount(Double totalAmountUndiscount) {
		this.totalAmountUndiscount = totalAmountUndiscount;
	}
	public Integer getCoupon_id() {
		return coupon_id;
	}
	public void setCoupon_id(Integer coupon_id) {
		this.coupon_id = coupon_id;
	}
	public Double getTotalAmountDiscount() {
		return totalAmountDiscount;
	}
	public void setTotalAmountDiscount(Double totalAmountDiscount) {
		this.totalAmountDiscount = totalAmountDiscount;
	}
	public Integer getUsePoints() {
		return usePoints;
	}
	public void setUsePoints(Integer usePoints) {
		this.usePoints = usePoints;
	}
	public Double getTotalAmountDiscountPoints() {
		return totalAmountDiscountPoints;
	}
	public void setTotalAmountDiscountPoints(Double totalAmountDiscountPoints) {
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
