package com.pet.model.member;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "favorites_products_view")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorites {
	
	@Id @Column(name = "favorite_id")
	private Integer favoriteId;
	
	@Column(name = "member_id")
	private Integer memberId;
	
	@Column(name = "product_id")
	private Integer productId;
	
	@Column(name = "product_name")
	private String productName;
	
	private Double price;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "favorite_created_at")
	private LocalDateTime createdAt;
	
}
