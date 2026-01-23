package com.pet.model.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pet.model.member.Member;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "cart_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "product_id"}) // 對應 SQL 的 Unique
})
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Integer cartItemId;

    // 關聯到會員 (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NonNull
    private Member member;

    // 關聯到商品 (ManyToOne)
    // FetchType.EAGER 是因為查購物車通常一定會要顯示商品圖跟名字
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "product_id")
    @NonNull
    private Product product;

    @Column(nullable = false)
    @NonNull
    private Integer quantity;

    @Column(name = "price_at_added")
    @NonNull
    private BigDecimal priceAtAdded;

    @CreationTimestamp // Hibernate 自動填入建立時間
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp // Hibernate 自動更新修改時間
    @Column(name = "updated_at")
    private Date updatedAt;

    // 此處請補上 Getter / Setter / 無參數 Constructor
}