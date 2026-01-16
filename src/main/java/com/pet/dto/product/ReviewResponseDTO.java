package com.pet.dto.product;

import java.time.LocalDateTime;

public class ReviewResponseDTO {

	private Integer reviewId;
    private String memberName; // 只顯示會員暱稱或名字
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    public ReviewResponseDTO(Integer reviewId, String memberName, Integer rating, String comment, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.memberName = memberName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }
    
    // Getters...
    public Integer getReviewId() { return reviewId; }
    public String getMemberName() { return memberName; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
