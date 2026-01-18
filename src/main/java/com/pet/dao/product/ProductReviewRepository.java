package com.pet.dao.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.product.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

	List<ProductReview> findByProduct_ProductIdOrderByCreatedAtDesc(Integer productId);
}
