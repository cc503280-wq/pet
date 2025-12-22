package com.pet.model.order;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity @Table(name = "OrderItems")
public class orderItemBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_item_id")
	private Integer productItemId;
	
	@ManyToOne
	@JoinColumn(name = "order_id",nullable=false)
	private orderBean order;
	
	@Column(name = "product_Id")
	private Integer productId;
	
	@Column(name = "quantity")
	private Integer quantity;
	
	@Column(name = "unit_price")
	private Double unitPrice;
	
	@Column(name = "subtotal")
	private Double subtotal;
	
	@Transient
	private String productName;
	
	public orderItemBean() {
		super();
	}

	

	public orderItemBean(Integer productItemId, orderBean orderId, Integer productId, Integer quantity, Double unitPrice,
			Double subtotal, String productName) {
		super();
		this.productItemId = productItemId;
		this.order = orderId;
		this.productId = productId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.subtotal = subtotal;
		this.productName = productName;
	}
	
	



	public orderItemBean(Integer productItemId, orderBean order, Integer productId, Integer quantity, Double unitPrice,
			Double subtotal) {
		super();
		this.productItemId = productItemId;
		this.order = order;
		this.productId = productId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.subtotal = subtotal;
	}



	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getProductItemId() {
		return productItemId;
	}

	public void setProductItemId(Integer productItemId) {
		this.productItemId = productItemId;
	}

	public orderBean getOrder() {
		return order;
	}

	public void setOrder(orderBean orderId) {
		this.order = orderId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Integer getOrderId() {
	    return order != null ? order.getOrderId() : null;
	}
	
	
	
	
	
	
	
	
	
}
