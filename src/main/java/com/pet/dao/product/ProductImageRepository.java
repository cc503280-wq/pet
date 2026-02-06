package com.pet.dao.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.product.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

	@Query("SELECT MAX(p.sortOrder) FROM ProductImage p WHERE p.product.productId = :productId")
    Integer findMaxSortOrderByProductId(Integer productId);
    
    // 確保抓取圖片時是依照順序排的
    List<ProductImage> findByProduct_ProductIdOrderBySortOrderAsc(Integer productId);
}
