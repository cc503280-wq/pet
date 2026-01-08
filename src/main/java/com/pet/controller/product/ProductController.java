package com.pet.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pet.model.product.Product;
import com.pet.service.product.ProductService;

@RestController // 1. 告訴 Spring 這是一個 REST API (會回傳 JSON)
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService pService;

	// 取得所有商品 (後台管理用)
	// 網址: GET /products/admin/all
	@GetMapping("/admin/all")
	public List<Product> getAllProducts() {
		return pService.findAllProducts();
	}

	// 取得上架商品
	@GetMapping
	public List<Product> getActiveProducts() {
		return pService.findActiveProducts();
	}

	// 取得下架商品
	// 網址: GET /products/admin/inactive
	@GetMapping("/admin/inactive")
	public List<Product> getInactiveProducts() {
		return pService.findNonActiveProducts();
	}

	// 取得單一商品詳情
	// 網址: GET /products/id
	@GetMapping("/{id}")
	public Product getProductById(@PathVariable Integer id) {
		return pService.getProductById(id);
	}

	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
		Product createdProduct = pService.createProduct(product);
		return ResponseEntity.ok(createdProduct); // 回傳 200 OK 與新商品資料
	}

	// 新增商品 (包含圖片)
	// ==========================================
	@PostMapping("/create")
	public ResponseEntity<?> createProduct(@RequestParam("productName") String productName,
			@RequestParam("description") String description, @RequestParam("price") Integer price,
			@RequestParam("stock") Integer stock, @RequestParam("categoryId") Integer categoryId,
			@RequestParam(value = "expireDate", required = false) String expireDate,

			// 處理檔案：MultipartFile
			@RequestParam(value = "file", required = false) MultipartFile file) {
		if (stock < 0) {
			stock = 0;
		}

		try {
			// 呼叫 Service 處理 (包含存圖片、存資料庫)
			Product newProduct = pService.createProductWithImage(productName, description, price, stock, categoryId,
					expireDate, file);
			return ResponseEntity.ok(newProduct);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("新增失敗: " + e.getMessage());
		}
	}

	// 上下架切換
	// 網址: PUT /products/5/status
	@PutMapping("/{id}/status")
	public ResponseEntity<String> toggleStatus(@PathVariable Integer id) {
		pService.toggleProductStatus(id);
		return ResponseEntity.ok("商品狀態更新成功");
	}

	// 搜尋商品 (模糊查詢)
	// 網址: GET /products/search?keyword=貓罐頭
	@GetMapping("/search")
	public List<Product> searchProducts(@RequestParam String keyword) {
		return pService.searchProducts(keyword);
	}

	// 根據分類查詢
	@GetMapping("/category/{categoryId}")
	public List<Product> getProductsByCategory(@PathVariable Integer categoryId) {
		return pService.getProductsByCategory(categoryId);
	}

	// 修改商品 (包含圖片更新)
	@PostMapping("/update/{id}")
	public ResponseEntity<?> updateProduct(@PathVariable Integer id, // 抓網址上的 ID
			@RequestParam("productName") String productName, @RequestParam("description") String description,
			@RequestParam("price") Integer price, @RequestParam("stock") Integer stock,
			@RequestParam("categoryId") Integer categoryId,
			@RequestParam(value = "expireDate", required = false) String expireDate,
			// 圖片是選填的，如果沒傳代表不改圖
			@RequestParam(value = "file", required = false) MultipartFile file) {
		
		if (stock < 0) {
			stock = 0;
		}

		try {
			Product updatedProduct = pService.updateProduct(id, productName, description, price, stock, categoryId,
					expireDate, file);

			if (updatedProduct != null) {
				return ResponseEntity.ok(updatedProduct);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(500).body("更新失敗: " + e.getMessage());
		}
	}

}
