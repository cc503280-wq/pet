package com.pet.dao.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pet.model.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByIsActive(Boolean isActive); //查詢 上架 / 下架 商品
	
	List<Product> findByProductNameContainingIgnoreCase(String keyword); //模糊查詢 (搜尋商品名稱)
	
	List<Product> findByCategory_CategoryId(Integer categoryId); //類別查詢 (找出某個分類下的所有商品)
	
	List<Product> findByIsActiveAndProductNameContainingIgnoreCase(Boolean isActive, String keyword);
	
	@Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isActive = :isActive WHERE p.productId = :id")
    void updateStatus(Integer id, Boolean isActive);  // 軟刪除 上下架變更
	
	Page<Product> findByIsActiveTrue(Pageable pageable);
	
}
