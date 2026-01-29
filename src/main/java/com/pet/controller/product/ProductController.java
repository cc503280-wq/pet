package com.pet.controller.product;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pet.dto.product.ProductStockDTO;
import com.pet.model.product.Category;
import com.pet.model.product.Product;
import com.pet.service.product.ProductService;
import com.pet.util.LoginUser;

@RestController // 1. 告訴 Spring 這是一個 REST API (會回傳 JSON)
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService pService;

	// 取得所有商品 (後台管理用)
	// 網址: GET /products/admin/all
	@GetMapping("/admin/all")
	public ResponseEntity<Page<Product>> getAllProducts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		// 🟢 呼叫 Service，而不是自己去 new PageRequest
		Page<Product> result = pService.getAllProductsWithPagination(page, size);

		return ResponseEntity.ok(result);
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
	public List<Product> searchProducts(@RequestParam String keyword,@RequestParam(required = false) Integer categoryId) {
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

	// 批量更新庫存
	// POST /products/batch-stock
	// Body: [ {"productId": 1, "newStock": 50}, {"productId": 2, "newStock": 99} ]
	@PostMapping("/batch-stock")
	public ResponseEntity<?> updateBatchStock(@RequestBody List<ProductStockDTO> stockList) {
		try {
			pService.batchUpdateStock(stockList);
			return ResponseEntity.ok("庫存批量更新成功！");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("更新失敗: " + e.getMessage());
		}
	}
	
	@GetMapping("/stats")
	@ResponseBody // 確保回傳 JSON
	public Map<String, Object> getProductStats() {
	    Map<String, Object> response = new HashMap<>();

	    // 1. 庫存告急 (stock < 10 的前 5 名)
	    // 這裡應該呼叫 Service -> Repository 查詢
	    // 模擬數據：
	    Map<String, Object> lowStock = new HashMap<>();
	    lowStock.put("labels", Arrays.asList("特級貓罐頭", "狗狗潔牙骨", "貓抓板", "餵食器", "貓草"));
	    lowStock.put("data", Arrays.asList(2, 5, 0, 1, 8));
	    
	    // 2. 分類統計
	    // 模擬數據：
	    Map<String, Object> categories = new HashMap<>();
	    categories.put("labels", Arrays.asList("貓食", "狗食", "玩具", "保健品"));
	    categories.put("data", Arrays.asList(120, 80, 45, 30));

	    response.put("lowStock", lowStock);
	    response.put("categories", categories);

	    return response;
	}

	// 給前台用的 API：只抓上架商品
	@GetMapping("/store/all")
	public ResponseEntity<Page<Product>> getStoreProducts(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "9") int size,
	        @RequestParam(required = false) Integer categoryId ,
	        @RequestParam(required = false) String keyword,
	        @RequestParam(required = false) Integer minPrice,
	        @RequestParam(required = false) Integer maxPrice,
	        @RequestParam(defaultValue = "new") String sort // 排序代號
	) {
		return ResponseEntity.ok(pService.getStoreProducts(page, size, categoryId, keyword, minPrice, maxPrice, sort));
	}

	@GetMapping("/store/categories")
	public ResponseEntity<List<Category>> getCategories() { // 回傳型態變了
	    return ResponseEntity.ok(pService.getAllCategories());
	}
	
	@GetMapping("/store/recommendations")
	public ResponseEntity<List<Product>> getRecommendations(@LoginUser Integer memberId) {
	    // Controller 只需要把 memberId (可能是 null) 傳給 Service
	    // Service 會自己判斷是訪客還是會員
	    return ResponseEntity.ok(pService.getRecommendations(memberId));
	}
	@GetMapping("/best-sellers")
	public ResponseEntity<List<Product>> getBestSellers() {
	    List<Product> list = pService.getBestSellers();
	    return ResponseEntity.ok(list);
	}
	
}
