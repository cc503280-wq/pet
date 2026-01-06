package com.pet.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.product.Category;
import com.pet.service.product.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryRestController {

	@Autowired
    private CategoryService categoryService;

    // 給 loadAllCategories() 用
	@GetMapping
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }

    // 2. 取得單筆 (GET /categories/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 3. 新增 (POST /categories/create)
    // 對應前端: formData.append('categoryName', ...)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestParam("categoryName") String categoryName) {
        try {
            categoryService.createCategory(categoryName);
            return ResponseEntity.ok("新增成功");
        } catch (RuntimeException e) {
            // 如果重複名稱，回傳 400 錯誤給前端
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. 修改 (POST /categories/update/{id})
    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, 
                                    @RequestParam("categoryName") String categoryName) {
        try {
            categoryService.updateCategory(id, categoryName);
            return ResponseEntity.ok("修改成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
