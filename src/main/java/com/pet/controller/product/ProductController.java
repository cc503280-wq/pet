package com.pet.controller.product;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.pet.service.product.ExcelExportService;
import com.pet.service.product.ProductService;
import com.pet.util.LoginUser;

/**
 * 商品控制器 (REST API)
 * 提供前端 (前台商城 + 後台管理系統) 呼叫的 JSON 接口
 */
@RestController // 告訴 Spring 這是一個 REST API (預設回傳 JSON)
@RequestMapping("/products") // 統一路由前綴，例如 /products/search
public class ProductController {

    @Autowired
    private ProductService pService;

    @Autowired
    private ExcelExportService excelExportService;

    // ==========================================
    // 後台管理 API (Admin Dashboard)
    // ==========================================

    /**
     * 取得所有商品列表 (後台分頁用)
     * GET /products/admin/all?page=0&size=10
     */
    @GetMapping("/admin/all")
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 呼叫 Service 進行分頁查詢
        Page<Product> result = pService.getAllProductsWithPagination(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 取得所有「已下架」的商品 (後台篩選用)
     * GET /products/admin/inactive
     */
    @GetMapping("/admin/inactive")
    public List<Product> getInactiveProducts() {
        return pService.findNonActiveProducts();
    }

    /**
     * 新增商品 (含圖片上傳)
     * POST /products/create
     * 接收 multipart/form-data 格式
     */
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(
            @RequestParam("productName") String productName,
            @RequestParam("description") String description, 
            @RequestParam("price") Integer price,
            @RequestParam("stock") Integer stock, 
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "expireDate", required = false) String expireDate,
            @RequestParam(value = "file", required = false) MultipartFile file) { // 圖片檔案

        if (stock < 0) stock = 0; // 防呆：庫存不可為負

        try {
            Product newProduct = pService.createProductWithImage(productName, description, price, stock, categoryId, expireDate, file);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("新增失敗: " + e.getMessage());
        }
    }

    /**
     * 修改商品 (含圖片更新)
     * POST /products/update/{id}
     * 接收 multipart/form-data 格式
     */
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestParam("productName") String productName, 
            @RequestParam("description") String description,
            @RequestParam("price") Integer price, 
            @RequestParam("stock") Integer stock,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "expireDate", required = false) String expireDate,
            @RequestParam(value = "file", required = false) MultipartFile file) { // 若為 null 代表不換圖

        if (stock < 0) stock = 0;

        try {
            Product updatedProduct = pService.updateProduct(id, productName, description, price, stock, categoryId, expireDate, file);
            if (updatedProduct != null) {
                return ResponseEntity.ok(updatedProduct);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新失敗: " + e.getMessage());
        }
    }

    /**
     * 切換商品上下架狀態
     * PUT /products/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<String> toggleStatus(@PathVariable Integer id) {
        pService.toggleProductStatus(id);
        return ResponseEntity.ok("商品狀態更新成功");
    }

    /**
     * 批量更新庫存 (後台快速編輯)
     * POST /products/batch-stock
     * Body: JSON Array [ {"productId": 1, "stock": 50}, ... ]
     */
    @PostMapping("/batch-stock")
    public ResponseEntity<?> updateBatchStock(@RequestBody List<ProductStockDTO> stockList) {
        try {
            pService.batchUpdateStock(stockList);
            return ResponseEntity.ok("庫存批量更新成功！");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新失敗: " + e.getMessage());
        }
    }

    /**
     * 取得後台儀表板統計數據 (圖表用)
     * GET /products/stats
     * 回傳: { "lowStock": {...}, "categories": {...} }
     */
    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = pService.getProductStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 匯出商品 Excel 報表
     * GET /products/export
     * 回傳: .xlsx 檔案流
     */
    @GetMapping("/export")
    public ResponseEntity<Resource> exportProducts() {
        List<Product> products = pService.findAllProducts();
        ByteArrayInputStream in = excelExportService.productsToExcel(products);
        
        String filename = "products_" + System.currentTimeMillis() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    // ==========================================
    // 通用查詢 API (Common Queries)
    // ==========================================

    /**
     * 取得單一商品詳情
     * GET /products/{id}
     */
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Integer id) {
        return pService.getProductById(id);
    }

    /**
     * 取得所有「上架中」商品 (簡單列表)
     * GET /products
     */
    @GetMapping
    public List<Product> getActiveProducts() {
        return pService.findActiveProducts();
    }

    /**
     * 搜尋商品 (模糊查詢名稱)
     * GET /products/search?keyword=xxx
     */
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword, @RequestParam(required = false) Integer categoryId) {
        // 目前這裡只實作了關鍵字搜尋，若需要多條件篩選建議改用 Service 的進階搜尋
        return pService.searchProducts(keyword);
    }

    /**
     * 根據分類 ID 取得商品
     * GET /products/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Integer categoryId) {
        return pService.getProductsByCategory(categoryId);
    }

    // ==========================================
    // 前台商城 API (Store Frontend)
    // ==========================================

    /**
     * 前台商品列表 (含分頁、搜尋、篩選、排序)
     * GET /products/store/all
     */
    @GetMapping("/store/all")
    public ResponseEntity<Page<Product>> getStoreProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "new") String sort // 排序代號: new, price_asc, price_desc
    ) {
        return ResponseEntity.ok(pService.getStoreProducts(page, size, categoryId, keyword, minPrice, maxPrice, sort));
    }

    /**
     * 取得所有商品分類 (供前台選單使用)
     * GET /products/store/categories
     */
    @GetMapping("/store/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(pService.getAllCategories());
    }

    /**
     * 取得個人化推薦商品
     * GET /products/store/recommendations
     * 需搭配 @LoginUser 註解解析 JWT Token 中的會員 ID
     */
    @GetMapping("/store/recommendations")
    public ResponseEntity<List<Product>> getRecommendations(@LoginUser Integer memberId) {
        // memberId 為 null 代表訪客，不為 null 代表已登入會員
        return ResponseEntity.ok(pService.getRecommendations(memberId));
    }

    /**
     * 取得熱銷商品 (首頁用)
     * GET /products/best-sellers
     */
    @GetMapping("/best-sellers")
    public ResponseEntity<List<Product>> getBestSellers() {
        List<Product> list = pService.getBestSellers();
        return ResponseEntity.ok(list);
    }
    
    // (createProduct 接收 JSON Body 的版本，目前較少用，通常用 multipart 版本)
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = pService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }
}