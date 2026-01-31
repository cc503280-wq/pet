package com.pet.dto.product; // 依您的 package 路徑

import lombok.Data;

@Data
public class CartItemDTO {
    private Integer cartItemId;
    private Integer productId;
    private String productName;
    private Integer price;
    private Integer quantity;
    private Integer stock;
    private String imageUrl;
    private Boolean isActive; 
}