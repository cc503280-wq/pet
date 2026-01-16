package com.pet.model.product;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pet.model.member.Member;

import jakarta.persistence.*;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name="product_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
	private Integer reviewId;
	@Column(name = "rating")
    private Integer rating; // 1-5 分

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // 多對一：一個商品有多個留言
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore // 避免 JSON 無窮迴圈
    private Product product;

    // 多對一：一個會員可以留多筆言
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore // 重要：回傳前端時，不要把會員的密碼等個資整包傳出去
    private Member member;

    // 自動寫入時間
    @PrePersist
    protected void onCreate() {
        // Date 用 new Date()
        // LocalDateTime 用 LocalDateTime.now()
        createdAt = LocalDateTime.now(); 
    }
}
