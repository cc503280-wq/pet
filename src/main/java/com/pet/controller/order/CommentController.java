package com.pet.controller.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // 新增這行
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // 新增這行
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dto.product.ReviewRequestDTO;
import com.pet.service.product.ProductReviewService;
import com.pet.util.LoginUser;

@RestController
@Transactional
@RequestMapping("/shop/comment")
public class CommentController { // 建議類別名稱首字大寫

    @Autowired
    private ProductReviewService productReviewService;

    @PostMapping("/post")
    // 加上 @RequestBody 才能正確解析前端傳來的 JSON List
    public ResponseEntity<String> addBatchReviews(@RequestBody List<ReviewRequestDTO> reviewRequestDTOs ,@LoginUser Integer memId) {
        
        // 您的 Service 邏輯保持不變，這裡用迴圈處理每一筆
        for (ReviewRequestDTO reviewRequestDTO : reviewRequestDTOs) {
            productReviewService.addReview(reviewRequestDTO,memId);
        }
        
        // 回傳 JSON 格式的成功訊息，而非 redirect
        return ResponseEntity.ok("評論已成功送出");
    }
}
