package com.pet.controller.product;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.product.Category;
import com.pet.service.product.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryCcontroller {

	@Autowired
	private CategoryService cService;

	@GetMapping
	public List<Category> getAllCategories() {
		return cService.getAllCategories();
	}

	// 取得單一分類 (用 ID)
	// 網址: GET /categories/1
	@GetMapping("/{id}")
	public Category getCategoryById(@PathVariable Integer id) {
		return cService.getCategoryById(id);
	}

	// 新增分類
	// 網址: POST /categories
	// Body (JSON): { "categoryName": "貓貓食品" }
	@PostMapping
	public ResponseEntity<Category> createCategory(@RequestBody Category category) {
		Category createdCategory = cService.createCategory(category);
		return ResponseEntity.ok(createdCategory);
	}

	// 修改分類名稱
	// 網址: PUT /categories/1
	// Body (JSON): { "categoryName": "貓貓與狗狗食品" }
	@PutMapping("/{id}")
	public ResponseEntity<Category> updateCategoryName(@PathVariable Integer id,
			@RequestBody Map<String, String> requestBody 
	) {
		String newName = requestBody.get("categoryName");

		Category updatedCategory = cService.updateCategoryName(id, newName);

		return ResponseEntity.ok(updatedCategory);
	}

}
