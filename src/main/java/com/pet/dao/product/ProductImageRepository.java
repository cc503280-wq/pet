package com.pet.dao.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.product.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

	List<ProductImage> findByProduct_ProductIdOrderBySortOrderAsc(Integer productId);
	
	@Query("SELECT MAX(p.sortOrder) FROM ProductImage p WHERE p.product.productId = :productId")
	Integer findMaxSortOrder(@Param("productId") Integer productId);
}
