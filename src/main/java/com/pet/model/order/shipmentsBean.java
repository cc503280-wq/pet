package com.pet.model.order;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity @Table(name = "shipments")
public class shipmentsBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shipment_id")
	private Integer shipmentId;
	
	@OneToOne
	@JoinColumn(name = "order_id")
	private orderBean order;
	
	@Column (name ="shipping_method")
	private String shippingMethod;
	
	@Column (name = "shipping_fee")
	private Integer shippingFee;
	
	@Column(name = "tracking_number")
	private String trackingNumber;
	
	@Column(name = "shipped_at")
	private Date shippedAt;
	
	@Column(name = "delivered_at")
	private Date deliveredAt;
	@Column(name = "status")
	private String status;
	
	@Column(name = "recipient_name")
	private String recipientName;
	@Column(name = "recipient_phone")
	private String recipientPhone;
	@Column(name = "shipping_Address")
	private String shippingAddress;
	
	
	public shipmentsBean(Integer shipmentId, orderBean orderId, String shippingMethod, Integer shippingFee,
			String trackingNumber, Date shippedAt, Date deliveredAt, String status, String recipientName,
			String recipientPhone, String shippingAddress) {
		super();
		this.shipmentId = shipmentId;
		this.order = orderId;
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
	public orderBean getorderId() {
		return order;
	}
	public void setorderId(orderBean orderId) {
		this.order = orderId;
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
	public Integer getOrderId() {
	    return order != null ? order.getOrderId() : null;
	}
	
	
	
	
	
}
