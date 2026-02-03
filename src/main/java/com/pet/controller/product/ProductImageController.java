package com.pet.controller.product;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pet.model.product.ProductImage;
import com.pet.service.product.ProductImageService;

@RestController
@RequestMapping("/api/product-images")
public class ProductImageController {

	@Autowired
	private ProductImageService imageService;

	// 1. 查詢該商品的所有附圖
	// 前端呼叫: GET /api/product-images/{productId}
	@GetMapping("/{productId}")
	public ResponseEntity<List<ProductImage>> getImages(@PathVariable Integer productId) {
		List<ProductImage> images = imageService.getImagesByProductId(productId);
		return ResponseEntity.ok(images);
	}

	// 2. 上傳圖片 (支援多檔)
	// 前端呼叫: POST /api/product-images/upload/{productId}
	// 參數: files (對應前端 FormData 的 key)
	@PostMapping("/upload/{productId}")
	public ResponseEntity<?> uploadImages(@PathVariable Integer productId,
			@RequestParam("files") MultipartFile[] files) {

		// 1. 基本檢查
		if (files == null || files.length == 0) {
			return ResponseEntity.badRequest().body(Map.of("message", "請選擇至少一張圖片"));
		}

		try {
			// 2. 呼叫 Service (這裡會處理 Cloudinary 上傳 + 自動排序)
			imageService.uploadImages(productId, files);

			// 3. 回傳成功 JSON
			return ResponseEntity.ok(Map.of("status", "success", "message", "圖片上傳成功"));

		} catch (Exception e) {
			e.printStackTrace(); // 方便後端除錯
			// 4. 回傳失敗 JSON
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", "上傳失敗: " + e.getMessage()));
		}
	}

	// 3. 刪除圖片
	// 前端呼叫: DELETE /api/product-images/{imageId}
	@DeleteMapping("/{imageId}")
	public ResponseEntity<?> deleteImage(@PathVariable Integer imageId) {
		try {
			imageService.deleteImage(imageId);
			return ResponseEntity.ok("刪除成功");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("刪除失敗");
		}
	}
}
