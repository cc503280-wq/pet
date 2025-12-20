package com.pet.model.member;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "favorites_products_view")
public class Favorites {
	
	@Id @Column(name = "favorite_id")
	private int favoriteId;
	
	@Column(name = "member_id")
	private int memberId;
	
	@Column(name = "product_id")
	private int productId;
	
	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "price")
	private double price;
	
	@Column(name = "created_at")
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

	public Favorites(int favoriteId, int memberId, int productId, String productName, double price) {
		super();
		this.favoriteId = favoriteId;
		this.memberId = memberId;
		this.productId = productId;
		this.productName = productName;
		this.price = price;
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
