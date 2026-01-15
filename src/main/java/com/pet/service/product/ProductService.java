package com.pet.service.product;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pet.dao.product.CategoryRepository;
import com.pet.dao.product.ProductRepository;
import com.pet.dto.product.ProductStockDTO;
import com.pet.model.product.Category;
import com.pet.model.product.Product;

@Service
@Transactional
public class ProductService {

	@Autowired
	private ProductRepository pRepos;

	@Autowired
	private CategoryRepository cRepos;

	@Autowired
	private Cloudinary cloudinary;
	
	// 查詢所有商品
	public List<Product> findAllProducts() {
		return pRepos.findAll();
	}

	// 查詢所有「上架中」商品
	public List<Product> findActiveProducts() {
		return pRepos.findByIsActive(true);
	}

	public List<Product> findNonActiveProducts() {
		return pRepos.findByIsActive(false);
	}

	// 查詢單一商品詳情
	public Product getProductById(Integer id) {
		Optional<Product> optionalProduct = pRepos.findById(id);

		if (optionalProduct.isPresent()) {
			return optionalProduct.get();
		} else {
			throw new RuntimeException("找不到商品 ID: " + id);
		}
	}

	// 新增商品
	public Product createProduct(Product product) {
		// 價格不能是負的
		if (product.getPrice().doubleValue() < 0) {
			throw new RuntimeException("商品價格不能為負數！");
		}
		// 庫存不能是負的
		if (product.getStock() < 0) {
			throw new RuntimeException("庫存數量不能為負數！");
		}

		product.setProductId(null);

		if (product.getIsActive() == null) {
			product.setIsActive(true);
		}

		return pRepos.save(product);
	}

	// 更新商品
	public Product updateProduct(Integer id, Product newProductData) {
		// 先去資料庫確認這個商品真的存在
		Product existingProduct = getProductById(id);

		// 更新欄位
		existingProduct.setProductName(newProductData.getProductName());
		existingProduct.setPrice(newProductData.getPrice());
		existingProduct.setDescription(newProductData.getDescription());
		existingProduct.setStock(newProductData.getStock());
		existingProduct.setImageUrl(newProductData.getImageUrl());
		existingProduct.setCategory(newProductData.getCategory());
		return pRepos.save(existingProduct);
	}

	// 軟刪除
	public void toggleProductStatus(Integer id) {
		// 先把商品查出來
		Product product = pRepos.findById(id).orElseThrow(() -> new RuntimeException("找不到商品 ID: " + id));

		// 取得目前的狀態，然後取反
		Boolean currentStatus = product.getIsActive();
		Boolean newStatus = !currentStatus;

		// 檢查庫存夠不夠
		if (newStatus == true && product.getStock() <= 0) {
			throw new RuntimeException("商品庫存為 0，無法上架！請先補貨。");
		}

		product.setIsActive(newStatus);

		pRepos.save(product);
	}

	// 模糊搜尋名稱
	public List<Product> searchProducts(String keyword) {
		return pRepos.findByProductNameContainingIgnoreCase(keyword);
	}

	// 根據分類找商品
	public List<Product> getProductsByCategory(Integer categoryId) {
		return pRepos.findByCategory_CategoryId(categoryId);
	}

	// 新增商品	
	public Product createProductWithImage(String name, String desc, Integer price, Integer stock, Integer catId,
			String expireDate, MultipartFile file) throws IOException {

		Product p = new Product();
		p.setProductName(name);
		p.setDescription(desc);
		p.setPrice(price);
		p.setStock(stock);
		p.setExpireDate(expireDate);

		// 關聯分類 (假設你有 CategoryRepository)
		if (catId != null) {
			Category category = cRepos.findById(catId).orElse(null);
			p.setCategory(category);
		}

		if (stock != null && stock > 0) {
            p.setIsActive(true);  // 有庫存 -> 預設上架
        } else {
            p.setIsActive(false); // 庫存為 0 (或負數) -> 自動下架
        }
		
		// 處理圖片
		if (file != null && !file.isEmpty()) {
			String fileName = saveFile(file); // 呼叫下面的小工具方法
			p.setImageUrl(fileName); // 資料庫只存 "檔名.jpg"
		}

		return pRepos.save(p);
	}

	// 2. 負責存檔案的小工具 (回傳新檔名)
	private String saveFile(MultipartFile file) throws IOException {
		// 設定存檔路徑 (建議存到 static 下面，這樣前端才讀得到)
		// 設定上傳選項 (例如指定資料夾名稱)
        Map params = ObjectUtils.asMap(
            "folder", "pet_shop_products", // 在 Cloudinary 裡的資料夾名稱
            "use_filename", true,          // 使用原始檔名
            "unique_filename", true        // 自動在檔名後加亂數避免重複
        );

        // 呼叫 Cloudinary 上傳
        // upload 方法回傳的是一個 Map，裡面包含很多資訊 (網址、大小、格式...)
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        // 取得安全連線的網址 (https://...)
        // 這會回傳像這樣的網址: https://res.cloudinary.com/demo/image/upload/v123/pet/cat.jpg
        String url = (String) uploadResult.get("secure_url");

        return url;
	}

	public Product updateProduct(Integer id, String name, String desc, Integer price, Integer stock, Integer catId,
			String expireDateStr, MultipartFile file) throws IOException {

		// 1. 找舊資料
		Product p = pRepos.findById(id).orElse(null);
		if (p == null)
			return null; // 找不到就回傳 null

		// 2. 更新欄位
		p.setProductName(name);
		p.setDescription(desc);
		p.setPrice(price);
		p.setStock(stock);
		p.setExpireDate(expireDateStr);
		p.setUpdatedAt(LocalDateTime.now()); // 更新時間

		// 更新分類
		if (catId != null) {
			Category category = cRepos.findById(catId).orElse(null);
			p.setCategory(category);
		}

		if (stock != null && stock > 0) {
	        // 這裡可以選擇：要「自動上架」還是「保持原狀」？
	        // 通常是：如果有貨了，就自動幫他上架
	        p.setIsActive(true); 
	    } else {
	        p.setIsActive(false); // 沒貨了一定要下架
	    }
		
		// 處理圖片 (關鍵邏輯)
		// 如果使用者有上傳新檔案才換圖
		if (file != null && !file.isEmpty()) {
			String fileName = saveFile(file);
			p.setImageUrl(fileName);
		}
		// 如果 file 是 null，代表使用者不想換圖 -> 這裡什麼都不做，保留舊圖

		return pRepos.save(p);
	}

	public void batchUpdateStock(List<ProductStockDTO> stockList) {
		
		
	    for (ProductStockDTO dto : stockList) {
	    	// 如果是負數，直接跳過這筆，不處理
	    	if (dto.getStock() < 0) {
	            continue; 
	        }
	    	
	        // 1. 先抓出商品
	        Product product = pRepos.findById(dto.getProductId())
	            .orElse(null); // 如果找不到就跳過，或拋出異常看你需求
	        
	        if (product != null) {
	            // 更新庫存
	            product.setStock(dto.getStock());
	            
	            // 庫存歸零自動下架
	            if (dto.getStock() == 0) {
	                product.setIsActive(false); 
	            }else if (dto.getStock() > 0) {
	                product.setIsActive(true); 
	            }
	            // 儲存 
	            pRepos.save(product);
	        }
	    }
	}
	
	public Page<Product> getAllProductsWithPagination(int page, int size) {
        // 1. 設定分頁與排序 (依照 ID 倒序，讓新商品在最上面)
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        
        // 2. 呼叫 Repository
        return pRepos.findAll(pageable);
    }
	
}
