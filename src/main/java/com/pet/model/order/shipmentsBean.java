package com.pet.model.order;

import java.util.Date;

public class shipmentsBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	Integer shipmentId;
	Integer orderId;
	String shippingMethod;
	Integer shippingFee;
	String trackingNumber;
	Date shippedAt;
	Date deliveredAt;
	String status;
	String recipientName;
	String recipientPhone;
	String shippingAddress;
	public shipmentsBean(Integer shipmentId, Integer orderId, String shippingMethod, Integer shippingFee,
			String trackingNumber, Date shippedAt, Date deliveredAt, String status, String recipientName,
			String recipientPhone, String shippingAddress) {
		super();
		this.shipmentId = shipmentId;
		this.orderId = orderId;
		this.shippingMethod = shippingMethod;
		this.shippingFee = shippingFee;
		this.trackingNumber = trackingNumber;
		this.shippedAt = shippedAt;
		this.deliveredAt = deliveredAt;
		this.status = status;
		this.recipientName = recipientName;
		this.recipientPhone = recipientPhone;
		this.shippingAddress = shippingAddress;
	}
	public shipmentsBean() {
		super();
	}
	public Integer getShipmentId() {
		return shipmentId;
	}
	public void setShipmentId(Integer shipmentId) {
		this.shipmentId = shipmentId;
	}
	public Integer getorderId() {
		return orderId;
	}
	public void setorderId(Integer orderId) {
		this.orderId = orderId;
	}
	public String getShippingMethod() {
		return shippingMethod;
	}
	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;
	}
	public Integer getShippingFee() {
		return shippingFee;
	}
	public void setShippingFee(Integer shippingFee) {
		this.shippingFee = shippingFee;
	}
	public String getTrackingNumber() {
		return trackingNumber;
	}
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	public Date getShippedAt() {
		return shippedAt;
	}
	public void setShippedAt(Date shippedAt) {
		this.shippedAt = shippedAt;
	}
	public Date getDeliveredAt() {
		return deliveredAt;
	}
	public void setDeliveredAt(Date deliveredAt) {
		this.deliveredAt = deliveredAt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRecipientName() {
		return recipientName;
	}
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	public String getRecipientPhone() {
		return recipientPhone;
	}
	public void setRecipientPhone(String recipientPhone) {
		this.recipientPhone = recipientPhone;
	}
	public String getShippingAddress() {
		return shippingAddress;
	}
	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
	
}
