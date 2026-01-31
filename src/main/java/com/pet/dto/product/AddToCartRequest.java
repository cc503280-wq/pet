package com.pet.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
//進來的資料
public class AddToCartRequest {

	@NotNull
    private Integer productId;
	@NotNull
	@Min(value = 1,message = "數量至少要1")
    private Integer quantity;
}
