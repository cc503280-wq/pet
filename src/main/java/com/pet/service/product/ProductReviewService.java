package com.pet.service.product;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dao.member.MemberRepository;
import com.pet.dao.product.ProductRepository;
import com.pet.dao.product.ProductReviewRepository;
import com.pet.dto.product.ReviewRequestDTO;
import com.pet.dto.product.ReviewResponseDTO;
import com.pet.model.member.Member;
import com.pet.model.product.Product;
import com.pet.model.product.ProductReview;

@Service
public class ProductReviewService {

	@Autowired
    private ProductReviewRepository reviewRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private MemberRepository memberRepo;
    
 // 1. 取得某商品的留言 (轉成 DTO)
    public List<ReviewResponseDTO> getReviewsByProduct(Integer productId) {
        List<ProductReview> reviews = reviewRepo.findByProduct_ProductIdOrderByCreatedAtDesc(productId);
        
        // 將 Entity 轉換成乾淨的 DTO
        return reviews.stream().map(r -> new ReviewResponseDTO(
            r.getReviewId(),
            r.getMember().getName(), // 假設 Member 有 getName()
            r.getRating(),
            r.getComment(),
            r.getCreatedAt()
        )).collect(Collectors.toList());
    }

    // 2. 新增留言
    public void addReview(ReviewRequestDTO dto) {
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        Member member = memberRepo.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("會員不存在"));

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setMember(member);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        
        reviewRepo.save(review);
    }
}
