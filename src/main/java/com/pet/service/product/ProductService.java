package com.pet.service.product;

import java.io.IOException;
import com.pet.aspect.LogAction;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

/**
 * 商品服務層 (Service Layer)
 * 負責處理商品相關的所有業務邏輯，包括 CRUD、庫存管理、圖片上傳、推薦系統與統計分析。
 */
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository pRepos; // 商品資料庫存取

    @Autowired
    private CategoryRepository cRepos; // 分類資料庫存取

    @Autowired
    private MemberPetRepository petRepo; // 會員寵物資料庫存取

    @Autowired
    private Cloudinary cloudinary; // 圖片雲端儲存服務

    @Autowired
    private LineNotificationServiceForAdmin lineNotify; // Line 通知服務

    // ==========================================
    // 基本查詢功能 (Basic Queries)
    // ==========================================

    /**
     * 查詢資料庫中所有商品
     */
    public List<Product> findAllProducts() {
        return pRepos.findAll();
    }

    /**
     * 查詢所有狀態為「上架中」(Active = true) 的商品
     */
    public List<Product> findActiveProducts() {
        return pRepos.findByIsActive(true);
    }

    /**
     * 查詢所有狀態為「未上架」(Active = false) 的商品
     */
    public List<Product> findNonActiveProducts() {
        return pRepos.findByIsActive(false);
    }

    /**
     * 根據 ID 查詢單一商品詳情
     * 若找不到則拋出 RuntimeException
     */
    public Product getProductById(Integer id) {
        Optional<Product> optionalProduct = pRepos.findById(id);

        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        } else {
            throw new RuntimeException("找不到商品 ID: " + id);
        }
    }

    // ==========================================
    // 商品管理功能 (CRUD)
    // ==========================================

    /**
     * 新增商品 (基本版，不含圖片上傳邏輯)
     * 包含價格與庫存的負數檢查
     */
    public Product createProduct(Product product) {
        // 價格不能是負的
        if (product.getPrice().doubleValue() < 0) {
            throw new RuntimeException("商品價格不能為負數！");
        }
        // 庫存不能是負的
        if (product.getStock() < 0) {
            throw new RuntimeException("庫存數量不能為負數！");
        }

        product.setProductId(null); // 確保是新增而非更新

        if (product.getIsActive() == null) {
            product.setIsActive(true); // 預設上架
        }

        return pRepos.save(product);
    }

    /**
     * 更新商品資料 (針對 Entity 物件)
     */
    public Product updateProduct(Integer id, Product newProductData) {
        // 先去資料庫確認這個商品真的存在
        Product existingProduct = getProductById(id);

        // 更新各個欄位
        existingProduct.setProductName(newProductData.getProductName());
        existingProduct.setPrice(newProductData.getPrice());
        existingProduct.setDescription(newProductData.getDescription());
        existingProduct.setStock(newProductData.getStock());
        existingProduct.setImageUrl(newProductData.getImageUrl());
        existingProduct.setCategory(newProductData.getCategory());
        return pRepos.save(existingProduct);
    }

    /**
     * 切換商品上下架狀態 (軟刪除/狀態變更)
     * 會檢查庫存：若庫存為 0，禁止手動上架
     */
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

    /**
     * 後台模糊搜尋 (依名稱)
     */
    public List<Product> searchProducts(String keyword) {
        return pRepos.findByProductNameContainingIgnoreCase(keyword);
    }

    /**
     * 根據分類 ID 篩選商品
     */
    public List<Product> getProductsByCategory(Integer categoryId) {
        return pRepos.findByCategory_CategoryId(categoryId);
    }

    // ==========================================
    // 圖片上傳與進階新增/修改 (Image Handling)
    // ==========================================

    /**
     * 新增商品 (含圖片上傳)
     * 1. 設定基本資料
     * 2. 自動判斷上下架狀態 (依庫存)
     * 3. 上傳圖片至 Cloudinary 並取得 URL
     */
    public Product createProductWithImage(String name, String desc, Integer price, Integer stock, Integer catId,
            String expireDate, MultipartFile file) throws IOException {

        Product p = new Product();
        p.setProductName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setStock(stock);
        p.setExpireDate(expireDate);

        // 關聯分類
        if (catId != null) {
            Category category = cRepos.findById(catId).orElse(null);
            p.setCategory(category);
        }

        // 自動判斷上架狀態
        if (stock != null && stock > 0) {
            p.setIsActive(true); // 有庫存 -> 預設上架
        } else {
            p.setIsActive(false); // 庫存為 0 (或負數) -> 自動下架
        }

        // 處理圖片上傳
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file); // 呼叫下方的 Cloudinary 工具方法
            p.setImageUrl(fileName); // 資料庫存 URL
        }

        return pRepos.save(p);
    }

    /**
     * 私有工具方法：將檔案上傳至 Cloudinary
     * @return 回傳圖片的安全連結 (https url)
     */
    private String saveFile(MultipartFile file) throws IOException {
        // 設定上傳參數
        Map params = ObjectUtils.asMap(
                "folder", "pet_shop_products", // Cloudinary 資料夾名稱
                "use_filename", true,          // 使用原始檔名
                "unique_filename", true        // 自動加亂數避免重複
        );

        // 上傳檔案
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        // 取得網址
        String url = (String) uploadResult.get("secure_url");

        return url;
    }

    /**
     * 修改商品 (含圖片上傳判斷)
     * 若使用者沒有上傳新圖片 (file == null)，則保留舊圖不變
     */
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
        p.setUpdatedAt(LocalDateTime.now()); // 更新最後修改時間

        // 更新分類
        if (catId != null) {
            Category category = cRepos.findById(catId).orElse(null);
            p.setCategory(category);
        }

        // 自動上下架邏輯
        if (stock != null && stock > 0) {
            // 有貨了，自動上架
            p.setIsActive(true);
        } else {
            // 沒貨了，強制下架
            p.setIsActive(false); 
        }

        // 處理圖片 (關鍵邏輯)
        // 只有當使用者上傳新檔案時才執行上傳並覆蓋 URL
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            p.setImageUrl(fileName);
        }
        // 如果 file 是 null，代表使用者不想換圖 -> 這裡什麼都不做，保留舊圖

        return pRepos.save(p);
    }

    // ==========================================
    // 庫存管理與通知 (Stock & Notifications)
    // ==========================================

    /**
     * 批量更新庫存
     * 包含 Line Notify 通知邏輯與自動上下架
     */
    public void batchUpdateStock(List<ProductStockDTO> stockList) {
        // 將 List 轉為 Map 以便快速查找，並過濾掉負數庫存
        Map<Integer, Integer> stockMap = stockList.stream()
                .filter(dto -> dto.getStock() >= 0)
                .collect(Collectors.toMap(ProductStockDTO::getProductId, ProductStockDTO::getStock));

        if (stockMap.isEmpty())
            return;

        List<Product> products = pRepos.findAllById(stockMap.keySet());

        products.forEach(product -> {
            Integer newStock = stockMap.get(product.getProductId());
            product.setStock(newStock);
            
            // 🔥 LINE 通知邏輯
            if (newStock == 0) {
                // 情境 A：庫存變成 0 -> 發送下架通知
                System.out.println("商品已下架：" + product.getProductName());
                lineNotify.sendOutOfStockAlert(product.getProductName());

            } else if (newStock < 5 && newStock > 0) {
                // 情境 B：庫存低於 5 但還沒光 -> 發送補貨警報
                System.out.println("觸發庫存警報：" + product.getProductName());
                lineNotify.sendStockAlert(product.getProductName(), newStock);
            }
            
            // 自動上下架邏輯：庫存 > 0 才上架
            product.setIsActive(newStock > 0);
        });

        pRepos.saveAll(products);
    }

    /**
     * 後台商品列表分頁
     */
    public Page<Product> getAllProductsWithPagination(int page, int size) {
        // 設定分頁與排序 (依照 ID 倒序，讓新商品在最上面)
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        return pRepos.findAll(pageable);
    }
    
    // ==========================================
    // 儀表板統計數據 (Dashboard Statistics)
    // ==========================================

    /**
     * 取得後台儀表板所需的統計數據 (Chart.js 格式)
     * 1. 庫存告急商品 (Top 5)
     * 2. 商品分類佔比
     */
    public Map<String, Object> getProductStats() {
        Map<String, Object> response = new HashMap<>();

        // --- 1. 庫存告急數據 (Low Stock) ---
        List<Product> lowStockList = pRepos.findTop5ByStockLessThanOrderByStockAsc(10);
        List<String> stockLabels = new ArrayList<>();
        List<Integer> stockData = new ArrayList<>();

        for (Product p : lowStockList) {
            String name = p.getProductName();
            // 字串截斷處理，避免圖表爆版
            if (name.length() > 8) name = name.substring(0, 8) + "...";
            stockLabels.add(name);
            stockData.add(p.getStock());
        }

        Map<String, Object> lowStockMap = new HashMap<>();
        lowStockMap.put("labels", stockLabels);
        lowStockMap.put("data", stockData);
        
        response.put("lowStock", lowStockMap);

        // --- 2. 商品分類佔比數據 (Category Distribution) ---
        List<Object[]> categoryCounts = pRepos.countProductsByCategory();
        
        List<String> catLabels = new ArrayList<>();
        List<Integer> catData = new ArrayList<>();

        for (Object[] row : categoryCounts) {
            // row[0] 是分類名稱, row[1] 是數量 (Count 回傳的是 Long)
            String categoryName = (String) row[0];
            Long count = (Long) row[1];

            catLabels.add(categoryName);
            catData.add(count.intValue());
        }

        Map<String, Object> catMap = new HashMap<>();
        catMap.put("labels", catLabels);
        catMap.put("data", catData);

        // 將分類數據放入回傳物件 (key 必須叫 "categories"，因為前端 JS 是這樣抓的)
        response.put("categories", catMap);

        return response;
    }

    // ==========================================
    // 前台商城功能 (Store Frontend)
    // ==========================================

    /**
     * 取得前台已上架商品 (分頁)
     */
    public Page<Product> getActiveProducts(int page, int size) {
        // 依 ID 新到舊排序
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").descending());
        return pRepos.findByIsActiveTrue(pageable);
    }

    /**
     * 取得所有分類 (供前台選單使用)
     */
    public List<Category> getAllCategories() {
        return pRepos.findDistinctCategories();
    }

    /**
     * 舊版前台商品查詢 (保留相容性)
     */
    public Page<Product> getStoreProducts(int page, int size, Integer categoryId, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        return pRepos.findShopProducts(categoryId, keyword, pageable);
    }

    /**
     * 🔥 前台萬用商品查詢 (包含價格區間、排序、關鍵字、分類)
     * 使用 @LogAction 記錄使用者搜尋行為
     */
    @LogAction(type = LogAction.ActionType.SEARCH) // [AOP] 紀錄搜尋行為
    public Page<Product> getStoreProducts(int page, int size, Integer categoryId, String keyword, Integer minPrice,
            Integer maxPrice, String sortCode) {

        // 1. 處理排序邏輯
        Sort sort = Sort.unsorted();
        if ("price_asc".equals(sortCode)) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equals(sortCode)) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "productId"); // 預設依新品排序
        }

        // 2. 建立分頁物件
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. 呼叫 Repository 的複雜查詢
        return pRepos.searchProducts(categoryId, keyword, minPrice, maxPrice, pageable);
    }

    /**
     * 💡 智慧推薦系統 (Smart Recommendations)
     * 根據會員擁有的寵物類型(貓/狗)與年齡，推薦相關商品
     */
    public List<Product> getRecommendations(Integer memberId) {
        // 1. 訪客處理：若無登入，直接回傳隨機商品
        if (memberId == null) {
            System.out.println(">> 訪客模式，直接回傳隨機商品");
            return pRepos.findRandomProducts();
        }

        // 2. 抓取會員寵物資料
        List<MemberPet> pets = petRepo.findByMemberMemberId(memberId);
        System.out.println(">> 找到寵物數量: " + pets.size());

        // 若會員沒填寫寵物資料，也回傳隨機商品
        if (pets.isEmpty()) {
            System.out.println(">> 會員無寵物，回傳隨機商品");
            return pRepos.findRandomProducts();
        }

        // 3. 準備推薦結果池 (使用 Set 避免重複商品)
        Set<Product> recommendationSet = new HashSet<>();

        // 4. 針對每一隻寵物進行精準與廣泛搜尋
        for (MemberPet pet : pets) {
            // 準備關鍵字清單 (擴充同義詞)
            List<String> typeKeywords = new ArrayList<>();
            String ageKey = "";

            // --- A. 判斷物種 ---
            if ("狗".equals(pet.getPetType())) {
                typeKeywords.add("犬"); // 抓: 幼犬, 全犬
                typeKeywords.add("狗"); // 抓: 狗零食, 狗狗罐頭
            } else if ("貓".equals(pet.getPetType())) {
                typeKeywords.add("貓"); // 貓通常就只有貓
            } else {
                continue; // 其他物種暫不處理
            }

            // --- B. 判斷年齡 ---
            if ("老年".equals(pet.getPetAge())) {
                ageKey = "老";
            } else if ("幼年".equals(pet.getPetAge())) {
                ageKey = "幼";
            } else {
                ageKey = "成";
            }

            // --- C. 核心搜尋 (跑迴圈搜所有同義詞) ---
            for (String typeKey : typeKeywords) {

                // 策略 1: 精準搜尋 (物種 + 年齡) e.g., "犬" + "老"
                List<Product> strictMatches = pRepos.findByTarget(typeKey, ageKey, PageRequest.of(0, 10));
                recommendationSet.addAll(strictMatches);

                // 策略 2: 廣泛搜尋 (只看物種) e.g., "犬" (不限年齡)
                List<Product> broadMatches = pRepos.findByTarget(typeKey, "", PageRequest.of(0, 10));
                recommendationSet.addAll(broadMatches);
            }
        }

        // 5. 轉換成 List 以便後續處理
        List<Product> finalResults = new ArrayList<>(recommendationSet);

        // 6. 如果推薦數量不足 4 筆，補隨機商品
        if (finalResults.size() < 4) {
            int need = 4 - finalResults.size();
            System.out.println(">> 數量不足 4 筆，需要補充 " + need + " 筆隨機商品");

            List<Product> randoms = pRepos.findRandomProducts();
            for (Product p : randoms) {
                if (finalResults.size() >= 4)
                    break;

                // 檢查 ID 是否已存在 (避免重複推薦)
                boolean exists = finalResults.stream()
                        .anyMatch(existing -> existing.getProductId().equals(p.getProductId()));
                if (!exists) {
                    finalResults.add(p);
                    System.out.println("   + 補充隨機: " + p.getProductName());
                }
            }
        }

        // 7. 洗牌 (讓每次顯示順序不同)
        Collections.shuffle(finalResults);

        // 8. 取前 4 筆回傳
        List<Product> result = finalResults.stream().limit(4).collect(Collectors.toList());
        return result;
    }
    
    /**
     * 取得熱銷商品 (Best Sellers)
     * 取前 8 名
     */
    public List<Product> getBestSellers() {
        // PageRequest.of(0, 8) 代表取第 0 頁，共 8 筆 (即前 8 名)
        return pRepos.findBestSellers(PageRequest.of(0, 8));
    }
}