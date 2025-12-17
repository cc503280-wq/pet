package com.pet.model.member;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class Favorites {
	private int favoriteId;
	private int memberId;
	private int productId;
	private String productName;
	private double price;
	private Timestamp createdAt;
	
	public Favorites() {
		super();
	}

	public Favorites(int favoriteId, int memberId, int productId, String productName, double price,
			Timestamp createdAt) {
		super();
		this.favoriteId = favoriteId;
		this.memberId = memberId;
		this.productId = productId;
		this.productName = productName;
		this.price = price;
		this.createdAt = createdAt;
	}

	public int getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(int favoriteId) {
		this.favoriteId = favoriteId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	
	
	
}
