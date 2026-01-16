package com.pet.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dto.product.ReviewRequestDTO;
import com.pet.dto.product.ReviewResponseDTO;
import com.pet.service.product.ProductReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ProductReviewController {

	@Autowired
    private ProductReviewService reviewService;

    // 1. 查看某商品的留言
    // GET /api/reviews/{productId}
    @GetMapping("/{productId}")
    public ResponseEntity<List<ReviewResponseDTO>> getProductReviews(@PathVariable Integer productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    // 2. 新增留言
    // POST /api/reviews
    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody ReviewRequestDTO request) {
        try {
            reviewService.addReview(request);
            return ResponseEntity.ok("留言成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("留言失敗: " + e.getMessage());
        }
    }
}
