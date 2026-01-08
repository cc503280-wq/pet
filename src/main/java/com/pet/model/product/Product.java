package com.pet.model.product;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity // 1. 告訴 Spring 這是一個對應資料庫的 Bean
@Table(name = "products") // 2. 對應資料庫的 table 名稱
@Data
@NoArgsConstructor // 4. 無參建構子
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {

	@Id // 主鍵
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Integer productId;

	@Column(name = "product_name", nullable = false)
	@NonNull
	private String productName;

	@Column(columnDefinition = "TEXT")
	private String description; // 商品描述

	@NonNull
	private Integer price;

	@NonNull
	private Integer stock; // 庫存

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	@NonNull
	private Category category;

	@Column(name = "image_url") // DB 通常是下底線，Java 用駝峰
	private String imageUrl;

	@Column(name = "expire_date")
	private String expireDate; // 對應 SQL 的 VARCHAR(40)，所以用 String

	@Column(name = "is_active")
	private Boolean isActive; // 對應 SQL 的 BIT (0或1)，Java 用 Boolean (true/false)

	@CreatedDate 
	@Column(name = "created_at", updatable = false) 
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime createdAt; 

	@LastModifiedDate 
	@Column(name = "updated_at") 
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime updatedAt;

}