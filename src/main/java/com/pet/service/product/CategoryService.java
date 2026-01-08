package com.pet.service.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dao.product.CategoryRepository;
import com.pet.model.product.Category;

@Service
@Transactional
public class CategoryService {

	@Autowired
	private CategoryRepository cRepos;

	// 查詢所有分類
	public List<Category> getAllCategories() {
		return cRepos.findAll();
	}

	// 查詢單一分類
	public Category getCategoryById(Integer id) {
		return cRepos.findById(id).orElseThrow(() -> new RuntimeException("找不到分類 ID: " + id));
	}

	// 新增分類 (含防呆)
	public Category createCategory(String name) {
        // 1. 檢查名字是否重複
        if (cRepos.existsByCategoryName(name)) {
            throw new RuntimeException("分類名稱已存在: " + name);
        }

        // 2. 建立新物件
        Category newCategory = new Category();
        newCategory.setCategoryName(name);

        return cRepos.save(newCategory);
    }
	// 改名
	public Category updateCategory(Integer id, String newName) {
        // 1. 先確認分類存在
        Category category = getCategoryById(id);

        // 如果新名字跟舊名字不一樣，才需要檢查重複
        if (!category.getCategoryName().equals(newName)) {
            if (cRepos.existsByCategoryName(newName)) {
                throw new RuntimeException("修改失敗，該分類名稱已存在: " + newName);
            }
            // 設定新名字
            category.setCategoryName(newName);
        }
        return cRepos.save(category);
    }
}
