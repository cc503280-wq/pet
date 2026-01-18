package com.pet.service.product;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pet.dao.product.ProductImageRepository;
import com.pet.dao.product.ProductRepository;
import com.pet.model.product.Product;
import com.pet.model.product.ProductImage;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductImageService {

	@Autowired
	private ProductImageRepository productImageRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private Cloudinary cloudinary;
	
	// 依據商品ID查詢商品的圖片
	public List<ProductImage> getImagesByProductId(Integer productId) {
        return productImageRepository.findByProduct_ProductIdOrderBySortOrderAsc(productId);
    }
	
	// 2. 批次上傳圖片
	public void uploadImages(Integer productId, MultipartFile[] files) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("找不到商品 ID: " + productId));

        // 計算目前的起始序號
        Integer maxOrder = productImageRepository.findMaxSortOrder(productId);
        
        // 邏輯說明：
        // Case A: 如果 maxOrder 是 null (完全沒圖)，我們設為 0。這樣迴圈進去後第一張就會變 1。
        // Case B: 如果 maxOrder 是 0 (只有封面)，currentOrder 就是 0。迴圈進去後第一張變 1。
        // Case C: 如果 maxOrder 是 5 (有封面+4張圖)，currentOrder 就是 5。迴圈進去後第一張變 6。
        int currentOrder = (maxOrder == null) ? 0 : maxOrder;
        
        // 如果 maxOrder 不是 null (代表已經有圖)，下一張要 +1
        // 如果 maxOrder 是 null (currentOrder=0)，下一張還是 0 (第一張就是封面)
        if (maxOrder != null) {
            currentOrder = maxOrder + 1;
        } else {
            currentOrder = 0;
        }
        // 用來標記「是否需要更新商品主圖」
        // 如果原本的 image_url 是空的，代表我們要把第一張新圖設為封面
        boolean needUpdateProductCover = (product.getImageUrl() == null || product.getImageUrl().trim().isEmpty());
        
        for (MultipartFile file : files) {
            try {                
                // 1. 準備參數
                Map params = ObjectUtils.asMap("folder", "pet-shop/gallery");
                
                // 2. 上傳並取得結果 Map
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
                
                // 3. 強制轉型取出網址 String
                String imageUrl = (String) uploadResult.get("secure_url");

                // 4. 存入資料庫
                ProductImage img = new ProductImage();
                img.setProduct(product);
                img.setImageUrl(imageUrl); // 這裡存進去的就是網址了！
                img.setSortOrder(currentOrder);

                if (needUpdateProductCover && currentOrder == 0) {
                    product.setImageUrl(imageUrl);
                    productRepository.save(product); // 更新商品主檔
                    
                    // 設定為 false，確保同一次批次上傳的第二、第三張圖不會覆蓋掉第一張
                    needUpdateProductCover = false; 
                }
                
                // 每次存檔前，先把序號 +1
                currentOrder++; 
                productImageRepository.save(img);

            } catch (IOException e) {
                throw new RuntimeException("上傳失敗");
            }
        }
    }
	
	// 刪除圖片
	public void deleteImage(Integer imageId) {
		// 1. 先從資料庫找出這筆資料 (因為我們需要它的 URL)
	    ProductImage img = productImageRepository.findById(imageId)
	            .orElseThrow(() -> new RuntimeException("找不到圖片 ID: " + imageId));

	    Product product = img.getProduct();
	    Integer productId = img.getProduct().getProductId();
	    // 2. 嘗試刪除 Cloudinary 上的檔案
	    try {
	        String publicId = getPublicIdFromUrl(img.getImageUrl());
	        
	        if (publicId != null) {
	            // 呼叫 Cloudinary 刪除 API
	            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
	            System.out.println("Cloudinary 刪除成功: " + publicId);
	        }
	    } catch (IOException e) {
	        // 注意：這裡我們只印出錯誤，但不阻擋資料庫刪除。
	        // 因為就算雲端刪除失敗，使用者還是希望這張圖從網站消失。
	        System.err.println("Cloudinary 刪除失敗: " + e.getMessage());
	    }
		
		productImageRepository.deleteById(imageId);
		// 強制執行一次 Flush，確保資料庫已經把那一行刪掉了
	    // 這樣下一步查詢時，才不會又查到剛剛刪掉的那筆
		productImageRepository.flush(); 

	    // 核心邏輯】重新排序剩下的圖片
	    // 抓出該商品剩下的所有圖，依照舊的順序排好
	    List<ProductImage> remainingImages = productImageRepository.findByProduct_ProductIdOrderBySortOrderAsc(productId);

	    // 跑迴圈，從 0 開始重新發牌
	    for (int i = 0; i < remainingImages.size(); i++) {
	        ProductImage p = remainingImages.get(i);
	        
	        // 如果目前的順序跟 i 不一樣，才需要更新 (節省效能)
	        if (p.getSortOrder() != i) {
	            p.setSortOrder(i);
	            productImageRepository.save(p);
	        }
	    }
	 // 🟢 5. 【新增邏輯】同步更新 Products 表格的封面圖
	    if (remainingImages.isEmpty()) {
	        // 情況 A: 圖片全刪光了，封面設為 null 或預設圖
	        product.setImageUrl(null); 
	    } else {
	        // 情況 B: 有剩下的圖，第一張 (index 0) 就是新的封面
	        // 因為上面迴圈已經把它的 sortOrder 改成 0 了
	        String newCoverUrl = remainingImages.get(0).getImageUrl();
	        product.setImageUrl(newCoverUrl);
	    }

	    // 6. 儲存商品 (更新 image_url 欄位)
	    productRepository.save(product);
    }
	
	// 輔助方法：從完整網址中解析出 public_id
	private String getPublicIdFromUrl(String url) {
	    try {
	        // 範例網址: https://res.cloudinary.com/demo/image/upload/v12345678/pet-shop/gallery/my-cat.jpg
	        
	        // 1. 找到 "upload/" 的位置並切掉前面的部分
	        // 結果: v12345678/pet-shop/gallery/my-cat.jpg
	        String path = url.substring(url.indexOf("upload/") + 7);
	        
	        // 2. 如果有版本號 (v開頭 + 數字 + /)，要把它切掉
	        // Cloudinary 的網址通常會有版本號，必須移除才能拿到正確的 public_id
	        if (path.startsWith("v") && path.indexOf("/") > 0) {
	            path = path.substring(path.indexOf("/") + 1);
	        }
	        // 結果: pet-shop/gallery/my-cat.jpg

	        // 3. 去掉副檔名 (.jpg, .png)
	        // Cloudinary 的 public_id 不包含副檔名
	        int dotIndex = path.lastIndexOf(".");
	        if (dotIndex > 0) {
	            path = path.substring(0, dotIndex);
	        }
	        
	        // 最終結果: pet-shop/gallery/my-cat
	        return path;
	    } catch (Exception e) {
	        // 如果解析失敗，回傳 null，避免程式崩潰
	        return null;
	    }
	}
}
