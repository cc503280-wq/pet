package com.pet.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.pet.service.product.CategoryService;
import com.pet.service.product.ProductService;

@Controller
public class ProductPageController {

	@Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/admin/products")
    public String productPage(Model model) {
        // 1. 抓取所有商品
        model.addAttribute("products", productService.findAllProducts());
        // 2. 抓取所有分類 (給下拉選單用)
        model.addAttribute("categories", categoryService.getAllCategories());
        
        // 3. 回傳 HTML 檔案名稱 (不需要加 .html)
        // 檔案要放在 src/main/resources/templates/admin/products.html
        return "admin/products"; 
    }
}
