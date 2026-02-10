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

		// 1. 確認商品存在
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("找不到商品 ID: " + productId));

		// 2. 查詢目前最大的序號 (如果沒有圖，預設為 0)
		Integer maxOrder = productImageRepository.findMaxSortOrderByProductId(productId);
		int currentOrder = (maxOrder == null) ? 0 : maxOrder;

		// 3. 跑迴圈處理每一張圖
		for (MultipartFile file : files) {
			if (file.isEmpty())
				continue; // 跳過空檔案

			try {
				// A. 上傳到 Cloudinary (假設您已經有這個 helper method)
				// 這裡的 uploadToCloudinary 是您原本封裝好的上傳邏輯
				String imageUrl = uploadToCloudinary(file);

				// B. 序號 +1
				currentOrder++;

				// C. 建立並儲存物件
				ProductImage image = new ProductImage();
				image.setProduct(product);
				image.setImageUrl(imageUrl);
				image.setSortOrder(currentOrder); // 🔥 設定我們算好的序號

				// (選用) 如果您有加上 publicId 欄位，建議也存進去，方便日後刪除
				// image.setPublicId( ... );

				productImageRepository.save(image);

			} catch (IOException e) {
				// 這裡可以選擇 log 錯誤並繼續傳下一張，或是直接拋出例外中斷全部
				throw new RuntimeException("圖片上傳失敗: " + file.getOriginalFilename(), e);
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
			// 範例網址:
			// https://res.cloudinary.com/demo/image/upload/v12345678/pet-shop/gallery/my-cat.jpg

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

	private String uploadToCloudinary(MultipartFile file) throws IOException {
		Map params = ObjectUtils.asMap("folder", "pet_shop_products", "use_filename", true, "unique_filename", true);
		Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
		return (String) uploadResult.get("url"); // 或 "secure_url"
	}
}
