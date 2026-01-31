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
		// 1. 先查出這張圖庫的圖片資料
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("找不到圖片 ID: " + imageId));

        // 2. 取得這張圖的網址
        String imageUrl = productImage.getImageUrl();
        
        // 3. 取得這張圖所屬的商品 (Product)
        Product product = productImage.getProduct();
        
        // 4. 🛑 關鍵檢查：這張圖的網址，是否等於該商品的「封面圖」網址？
        boolean isUsedAsCover = false;
        if (product.getImageUrl() != null && product.getImageUrl().equals(imageUrl)) {
            isUsedAsCover = true;
        }

        // 5. 決定是否刪除雲端檔案
        if (isUsedAsCover) {
            // A. 如果是封面圖 -> 【只刪資料庫，保留雲端檔案】
            System.out.println("此圖片同時為封面圖，僅移除圖庫關聯，保留雲端檔案: " + imageUrl);
        } else {
            // B. 如果不是封面圖 -> 【刪除雲端檔案】
            try {
                // 從網址中解析出 Cloudinary 的 publicId
                String publicId = getPublicIdFromUrl(imageUrl);
                if (publicId != null) {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                    System.out.println("已刪除雲端檔案: " + publicId);
                }
            } catch (Exception e) {
                System.err.println("雲端圖片刪除失敗 (可能已不存在): " + e.getMessage());
                // 這裡可以選擇是否要拋出異常，通常建議吞掉異常繼續刪除資料庫紀錄
            }
        }

        // 6. 最後：一定要刪除圖庫資料表的紀錄
        productImageRepository.delete(productImage);
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
