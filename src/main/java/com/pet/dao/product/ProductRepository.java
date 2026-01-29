package com.pet.dao.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pet.model.product.Category;
import com.pet.model.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	// ==========================================
	// 基本查詢 (原有)
	// ==========================================
	List<Product> findByIsActive(Boolean isActive); // 查詢 上架 / 下架 商品

	List<Product> findByProductNameContainingIgnoreCase(String keyword); // 模糊查詢 (搜尋商品名稱)

	List<Product> findByCategory_CategoryId(Integer categoryId); // 類別查詢 (找出某個分類下的所有商品)

	List<Product> findByIsActiveAndProductNameContainingIgnoreCase(Boolean isActive, String keyword);

	@Modifying
	@Transactional
	@Query("UPDATE Product p SET p.isActive = :isActive WHERE p.productId = :id")
	void updateStatus(Integer id, Boolean isActive); // 軟刪除 上下架變更

	// 找出所有不重複的分類名稱
	@Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true")
	List<Category> findDistinctCategories();

	// ==========================================
	// 🟢 後台管理搜尋用 (之前 Service 有呼叫到，補上以防報錯)
	// ==========================================

	// 1. 純關鍵字搜尋 (後台用，不限上架狀態)
	List<Product> findByProductNameContaining(String productName);

	// 2. 關鍵字 + 分類 ID 搜尋 (後台用，不限上架狀態)
	List<Product> findByProductNameContainingAndCategory_CategoryId(String productName, Integer categoryId);

	// ==========================================
	// 🟢 前台商品列表專用 (必須上架 + 有庫存)
	// ==========================================
	/**
	 * 智慧型查詢：同時處理「分類」、「關鍵字」與「庫存檢查」 邏輯： 1. 必須上架 (isActive = true) 2. 必須有庫存 (stock >
	 * 0) 3. 如果 categoryId 是 null，就略過分類條件 4. 如果 keyword 是 null，就略過關鍵字條件
	 */
	@Query("SELECT p FROM Product p WHERE " + "p.isActive = true AND p.stock > 0 "
			+ "AND (:categoryId IS NULL OR p.category.categoryId = :categoryId) "
			+ "AND (:keyword IS NULL OR p.productName LIKE %:keyword% OR p.description LIKE %:keyword%)")
	Page<Product> findShopProducts(@Param("categoryId") Integer categoryId, @Param("keyword") String keyword,
			Pageable pageable);

	// ==========================================
	// 🟢 萬用查詢 (Sort, Filter) - 已加入庫存判斷
	// ==========================================
	@Query("SELECT p FROM Product p WHERE " + "p.isActive = true AND " + "p.stock > 0 AND " + // 🔥 關鍵修改：只查庫存 > 0
			"(:categoryId IS NULL OR p.category.categoryId = :categoryId) AND "
			+ "(:keyword IS NULL OR p.productName LIKE %:keyword% OR p.description LIKE %:keyword%) AND "
			+ "(:minPrice IS NULL OR p.price >= :minPrice) AND " + "(:maxPrice IS NULL OR p.price <= :maxPrice)")
	Page<Product> searchProducts(@Param("categoryId") Integer categoryId, @Param("keyword") String keyword,
			@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice, Pageable pageable);

	// ==========================================
	// 🟢 推薦系統 (已加入庫存判斷)
	// ==========================================

	// 1. 訪客用：隨機推薦 (Native Query)
	// 🔥 修改：加入 AND stock > 0
	@Query(value = "SELECT TOP 4 * FROM products WHERE is_active = 1 AND stock > 0 ORDER BY NEWID()", nativeQuery = true)
	List<Product> findRandomProducts();

	// 2. 會員用：根據關鍵字列表推薦
	// 🔥 修改：加入 AND p.stock > 0
	@Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stock > 0 AND " + "(p.productName LIKE %:type%) AND "
			+ "(p.productName LIKE %:age% OR p.description LIKE %:age%)")
	List<Product> findByTarget(@Param("type") String type, @Param("age") String age, Pageable pageable);

	// (保留舊方法以免舊程式碼報錯，但建議改用上面帶 Stock 判斷的新方法)
	Page<Product> findByIsActiveTrue(Pageable pageable);

	@Query(value = """
	           SELECT p.product_id, p.product_name, p.description, p.price, p.stock, 
	                  p.category_id, p.image_url, p.expire_date, p.is_active, p.created_at, p.updated_at 
	           FROM products p 
	           INNER JOIN (
	               SELECT product_id, SUM(quantity) as total_qty 
	               FROM Order_Items 
	               GROUP BY product_id
	           ) sales ON p.product_id = sales.product_id 
	           ORDER BY sales.total_qty DESC
	           """, 
	           nativeQuery = true)
	    List<Product> findBestSellers(Pageable pageable);
}