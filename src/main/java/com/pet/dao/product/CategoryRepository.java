package com.pet.dao.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pet.model.product.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	// 輔助改名/新增功能
	Optional<Category> findByCategoryName(String categoryName);

	// 用途：回傳 true/false 純檢查用
	boolean existsByCategoryName(String categoryName);
}
