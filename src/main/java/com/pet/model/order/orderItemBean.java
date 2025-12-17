package com.pet.model.order;

import java.io.Serializable;

public class orderItemBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer productItemId;
	private Integer orderId;
	private Integer productId;
	private Integer quantity;
	private Double unitPrice;
	private Double subtotal;
	private String productName;
	
	public orderItemBean() {
		super();
	}

	

	public orderItemBean(Integer productItemId, Integer orderId, Integer productId, Integer quantity, Double unitPrice,
			Double subtotal, String productName) {
		super();
		this.productItemId = productItemId;
		this.orderId = orderId;
		this.productId = productId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.subtotal = subtotal;
		this.productName = productName;
	}
	
	



	public orderItemBean(Integer productItemId, Integer orderId, Integer productId, Integer quantity, Double unitPrice,
			Double subtotal) {
		super();
		this.productItemId = productItemId;
		this.orderId = orderId;
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

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
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
	
	
	
	
	
	
	
	
	
	
}
