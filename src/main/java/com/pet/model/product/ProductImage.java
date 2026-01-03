package com.pet.model.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore; // 避免無限迴圈

@Entity
@Table(name = "product_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 對應 IDENTITY(1,1)
    @Column(name = "image_id")
    private Integer imageId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "sort_order")
    private Integer sortOrder; // 排序用 (例如第1張是封面圖)

    // ==========================================
    // 🔥 重點：關聯設定 (多對一)
    // ==========================================
    // 這裡對應 SQL 的 FOREIGN KEY (product_id)
    // 意思：這張圖片屬於哪一個商品
    @ManyToOne(fetch = FetchType.LAZY) // 建議用 LAZY，除非你查圖片時一定都要用到商品詳細資料
    @JoinColumn(name = "product_id", nullable = false) 
    @ToString.Exclude // 避免 Lombok toString 無限迴圈
    @JsonIgnore // 避免 JSON 轉檔無限迴圈 (Product -> Images -> Product...)
    private Product product;
}