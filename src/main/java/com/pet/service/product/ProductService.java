package com.pet.service.product;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.pet.dao.member.MemberPetRepository;
import com.pet.dao.product.CategoryRepository;
import com.pet.dao.product.ProductRepository;
import com.pet.dto.product.ProductStockDTO;
import com.pet.model.member.MemberPet;
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
	private MemberPetRepository petRepo;
	
	@Autowired
	private Cloudinary cloudinary;
	
	@Autowired
    private LineNotificationServiceForAdmin lineNotify;
	
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

	//批量改庫存
	public void batchUpdateStock(List<ProductStockDTO> stockList) {
        
        // ... (這裡保留您原本轉 Map 的程式碼) ...
        Map<Integer, Integer> stockMap = stockList.stream()
            .filter(dto -> dto.getStock() >= 0)
            .collect(Collectors.toMap(ProductStockDTO::getProductId, ProductStockDTO::getStock));

        if (stockMap.isEmpty()) return;

        List<Product> products = pRepos.findAllById(stockMap.keySet());

        products.forEach(product -> {
            Integer newStock = stockMap.get(product.getProductId());
            product.setStock(newStock);
            
            // ==========================================
            // 🔥 修改後的 LINE 通知邏輯
            // ==========================================
            
            if (newStock == 0) {
                // 情境 A：庫存變成 0 -> 發送下架通知
                System.out.println("商品已下架：" + product.getProductName());
                lineNotify.sendOutOfStockAlert(product.getProductName());
                
            } else if (newStock < 5 && newStock > 0) {
                // 情境 B：庫存低於 5 但還沒光 -> 發送補貨警報
                System.out.println("觸發庫存警報：" + product.getProductName());
                lineNotify.sendStockAlert(product.getProductName(), newStock);
            }
            
            // ==========================================

            // 自動上下架邏輯
            product.setIsActive(newStock > 0);
        });

        pRepos.saveAll(products);
    }
	
	public Page<Product> getAllProductsWithPagination(int page, int size) {
        // 1. 設定分頁與排序 (依照 ID 倒序，讓新商品在最上面)
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        
        // 2. 呼叫 Repository
        return pRepos.findAll(pageable);
    }
	
	
	// 前台功能
	public Page<Product> getActiveProducts(int page, int size) {
	    // 依 ID 新到舊排序
	    Pageable pageable = PageRequest.of(page, size, Sort.by("productId").descending());
	    return pRepos.findByIsActiveTrue(pageable);
	}
	
	public List<Category> getAllCategories() {
	    return pRepos.findDistinctCategories();
	}

	
    // --- 功能 2: 取得前台商品 (包含分頁與分類邏輯) ---
	// --- 功能 2: 取得前台商品 (包含分頁與分類邏輯) ---
    // 這看起來是舊版的查詢方法，為了保險起見我們也一起改
	public Page<Product> getStoreProducts(int page, int size, Integer categoryId, String keyword) {
        Pageable pageable = PageRequest.of(page, size);

        // 🟢 濃縮後的寫法：
        // 不管前端傳什麼 (null 或 有值)，這個方法都能自動處理
        return pRepos.findShopProducts(categoryId, keyword, pageable);
    }

    // 這個是您目前主要使用的萬用查詢方法 (包含價格區間與排序)
	public Page<Product> getStoreProducts(int page, int size, Integer categoryId, String keyword, Integer minPrice, Integer maxPrice, String sortCode) {
	    // 1. 處理排序邏輯 (保持不變)
	    Sort sort = Sort.unsorted();
	    if ("price_asc".equals(sortCode)) {
	        sort = Sort.by(Sort.Direction.ASC, "price");
	    } else if ("price_desc".equals(sortCode)) {
	        sort = Sort.by(Sort.Direction.DESC, "price");
	    } else {
	        sort = Sort.by(Sort.Direction.DESC, "productId");
	    }

	    // 2. 建立分頁物件 (保持不變)
	    Pageable pageable = PageRequest.of(page, size, sort);

	    // 3. 呼叫萬用查詢
        // 🟢 我們需要在 Repository 的 @Query 中確認是否已經加上了 stock > 0 的判斷
	    return pRepos.searchProducts(categoryId, keyword, minPrice, maxPrice, pageable);
	}
	
	public List<Product> getRecommendations(Integer memberId) {
	    // 1. 訪客處理
	    if (memberId == null) {
	        System.out.println(">> 訪客模式，直接回傳隨機商品");
	        return pRepos.findRandomProducts();
	    }

	    // 2. 抓取會員寵物
	    List<MemberPet> pets = petRepo.findByMemberMemberId(memberId);
	    System.out.println(">> 找到寵物數量: " + pets.size());

	    if (pets.isEmpty()) {
	        System.out.println(">> 會員無寵物，回傳隨機商品");
	        return pRepos.findRandomProducts();
	    }

	    // 3. 準備大池子
	    Set<Product> recommendationSet = new HashSet<>();

	    // 4. 針對每一隻寵物搜尋
	    for (MemberPet pet : pets) {
	        // 準備一個清單來放「物種關鍵字」，因為狗可能有兩種講法
	        List<String> typeKeywords = new ArrayList<>();
	        String ageKey = "";

	        // --- 1. 判斷物種 (擴充同義詞) ---
	        if ("狗".equals(pet.getPetType())) {
	            typeKeywords.add("犬"); // 抓: 幼犬, 全犬
	            typeKeywords.add("狗"); // 抓: 狗零食, 狗狗罐頭
	        } else if ("貓".equals(pet.getPetType())) {
	            typeKeywords.add("貓"); // 貓通常就只有貓
	        } else {
	            continue;
	        }

	        // --- 2. 判斷年齡 ---
	        if ("老年".equals(pet.getPetAge())) {
	            ageKey = "老";
	        } else if ("幼年".equals(pet.getPetAge())) {
	            ageKey = "幼";
	        } else {
	            ageKey = "成";
	        }

	        // --- 3. 核心搜尋 (跑迴圈搜所有同義詞) ---
	        // 因為 "狗" 跟 "犬" 都要搜，所以這裡再多一層迴圈
	        for (String typeKey : typeKeywords) {
	            
	            // 策略 A: 精準搜尋 (物種 + 年齡)
	            // 例如：先找 "犬"+"老"，再找 "狗"+"老"
	        	List<Product> strictMatches = pRepos.findByTarget(typeKey, ageKey, PageRequest.of(0, 10));
	        	recommendationSet.addAll(strictMatches);

	            // 策略 B: 廣泛搜尋 (只看物種)
	            // 例如：先找 "犬" (不限年齡)，再找 "狗" (不限年齡)
	        	List<Product> broadMatches = pRepos.findByTarget(typeKey, "", PageRequest.of(0, 10));
	        	recommendationSet.addAll(broadMatches);
	        }
	    }

	    // 5. 轉換成 List
	    List<Product> finalResults = new ArrayList<>(recommendationSet);

	    // 6. 補隨機商品
	    if (finalResults.size() < 4) {
	        int need = 4 - finalResults.size();
	        System.out.println(">> 數量不足 4 筆，需要補充 " + need + " 筆隨機商品");
	        
	        List<Product> randoms = pRepos.findRandomProducts();
	        for (Product p : randoms) {
	            if (finalResults.size() >= 4) break;
	            
	            // 檢查 ID 是否已存在
	            boolean exists = finalResults.stream().anyMatch(existing -> existing.getProductId().equals(p.getProductId()));
	            if (!exists) {
	                finalResults.add(p);
	                System.out.println("   + 補充隨機: " + p.getProductName());
	            }
	        }
	    }

	    // 7. 洗牌
	    Collections.shuffle(finalResults);
	    
	    // 8. 取前 4 筆回傳
	    List<Product> result = finalResults.stream().limit(4).collect(Collectors.toList());
	    return result;
	} 
	
	
}
