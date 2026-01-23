package com.pet.dao.product;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CartItemResponse {

    private Integer cartItemId;  // 購物車項目 ID (刪除/更新用)
    private Integer productId;   // 商品 ID (點擊跳轉用)
    private String productName;  // 商品名稱
    private String imageUrl;     // 商品圖片
    private Integer price;       // 單價 (這裡假設您的價格是 Integer，如果是 BigDecimal 請自行替換)
    private Integer quantity;    // 購買數量
    private Integer subtotal;    // 🔥 小計 (單價 * 數量) - 前端直接顯示這個很方便
    private Integer stock;       // 庫存 (前端可用來限制 "+" 按鈕不能超過庫存)
}
