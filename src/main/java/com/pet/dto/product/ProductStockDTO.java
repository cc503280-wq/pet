package com.pet.dto.product;

public class ProductStockDTO {
	private Integer productId; // 必須叫 productId，不能叫 product_id 或 id
    private Integer stock;     // 必須叫 stock

    // ⚠️ 非常重要：一定要有 Getter 和 Setter！
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
