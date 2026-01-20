package com.pet.model.product;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pet.model.product.Product; // 記得引入妳的 Product Entity

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorites") // 🟢 對應妳剛剛建立的真實表格
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 對應 SQL 的 IDENTITY(1,1)
    @Column(name = "favorite_id")
    private Integer favoriteId;

    @Column(name = "member_id")
    private Integer memberId;

    // 🟢 關鍵：利用 JPA 關聯商品表
    // insertable=false, updatable=false 是因為我們下面還有一個 product_id 欄位(可選)，
    // 但通常建議直接用物件操作。這裡我們用 @JoinColumn 來映射。
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id") 
    private Product product;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", insertable = false, updatable = false) 
    // insertable=false 讓資料庫的 DEFAULT GETDATE() 生效
    private LocalDateTime createdAt;
}