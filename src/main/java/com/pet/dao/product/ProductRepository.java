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

	List<Product> findByIsActive(Boolean isActive); // 查詢 上架 / 下架 商品

	List<Product> findByProductNameContainingIgnoreCase(String keyword); // 模糊查詢 (搜尋商品名稱)

	List<Product> findByCategory_CategoryId(Integer categoryId); // 類別查詢 (找出某個分類下的所有商品)

	List<Product> findByIsActiveAndProductNameContainingIgnoreCase(Boolean isActive, String keyword);

	@Modifying
	@Transactional
	@Query("UPDATE Product p SET p.isActive = :isActive WHERE p.productId = :id")
	void updateStatus(Integer id, Boolean isActive); // 軟刪除 上下架變更

	Page<Product> findByIsActiveTrue(Pageable pageable);

	// 根據「分類」找上架商品
	Page<Product> findByCategory_CategoryIdAndIsActiveTrue(Integer categoryId, Pageable pageable);

	// 找出所有不重複的分類名稱
	@Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true")
	List<Category> findDistinctCategories();

	// 根據「商品名稱」模糊搜尋 + 必須是上架中
	Page<Product> findByProductNameContainingAndIsActiveTrue(String keyword, Pageable pageable);

	// 同時篩選「分類 ID」 + 「商品名稱 (模糊搜尋)」 + 「上架中」
	Page<Product> findByCategory_CategoryIdAndProductNameContainingAndIsActiveTrue(Integer categoryId, String keyword,
			Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.isActive = true "
			+ "AND (:categoryId IS NULL OR p.category.categoryId = :categoryId) "
			+ "AND (:keyword IS NULL OR p.productName LIKE %:keyword%) "
			+ "AND (:minPrice IS NULL OR p.price >= :minPrice) " + "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
	Page<Product> searchProducts(@Param("categoryId") Integer categoryId, @Param("keyword") String keyword,
			@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice, Pageable pageable);
	

	// 1. 訪客用：隨機推薦 (Native Query 效能較好)
    @Query(value = "SELECT TOP 4 * FROM products WHERE is_active = 1 ORDER BY NEWID()", nativeQuery = true)
    List<Product> findRandomProducts();

    // 2. 會員用：根據關鍵字列表推薦 (只要符合任一關鍵字即可)
    // 這裡使用 DISTINCT 避免重複商品
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
    	       "(p.productName LIKE %:type%) AND " + // 🔴 改這裡：物種只準搜「商品名稱」
    	       "(p.productName LIKE %:age% OR p.description LIKE %:age%)")
    	List<Product> findByTarget(@Param("type") String type, 
    	                           @Param("age") String age, 
    	                           Pageable pageable);
}
