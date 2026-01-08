package com.pet.model.order;

import java.util.Date;

public class CartItemsBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private int cart_item_id;
	private int member_id;
	private int product_id;
	private int quantity;
	private Double price_at_added;
	private Date created_at;
	private Date updated_at;

	public int getCart_item_id() {
		return cart_item_id;
	}

	public void setCart_item_id(int cart_item_id) {
		this.cart_item_id = cart_item_id;
	}

	public int getMember_id() {
		return member_id;
	}

	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Double getPrice_at_added() {
		return price_at_added;
	}

	public void setPrice_at_added(Double price_at_added) {
		this.price_at_added = price_at_added;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

}