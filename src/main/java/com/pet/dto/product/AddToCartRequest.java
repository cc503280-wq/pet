package com.pet.dto.product;

import lombok.Data;

@Data
//進來的資料
public class AddToCartRequest {

    private Integer productId;
    private Integer quantity;
}
