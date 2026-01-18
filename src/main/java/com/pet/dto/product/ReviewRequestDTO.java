package com.pet.dto.product;

public class ReviewRequestDTO {
	private Integer productId;
    private Integer memberId; // 實際專案中通常是從 Session/Token 抓，但練習時可先從前端傳
    private Integer rating;
    private String comment;

    // Getters and Setters
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
