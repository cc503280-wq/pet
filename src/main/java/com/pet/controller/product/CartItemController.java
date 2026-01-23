package com.pet.controller.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dao.product.CartItemResponse;
import com.pet.dto.product.AddToCartRequest;
import com.pet.service.product.CartItemService;
import com.pet.util.LoginUser;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartItemController {
	
	@Autowired
    private CartItemService cartService;
	// ✅ 1. 加入購物車
    @PostMapping("/add")
    public String addToCart(@LoginUser Integer memberId, @RequestBody AddToCartRequest request) {
        // 模擬從 Token 取得 memberId (實際上您要寫解析 Token 的邏輯)
    	if (memberId == null) {
            throw new RuntimeException("請先登入"); // 或回傳 401
        }
        cartService.addToCart(memberId, request.getProductId(), request.getQuantity());
        return "加入成功";
    }

    // ✅ 2. 查看我的購物車 (回傳 DTO List)
    @GetMapping("/my-cart")
    public ResponseEntity<List<CartItemResponse>> getMyCart(@LoginUser Integer memberId) {
        // Controller 只要負責把 ID 丟給 Service
        // 所有的檢查、轉換、計算，Service 都幫你做好了
        List<CartItemResponse> cartList = cartService.getMyCart(memberId);
        
        return ResponseEntity.ok(cartList);
    }
    
    // ✅ 3. 移除商品
    @DeleteMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Integer productId) {
        Integer memberId = 1; 
        cartService.removeFromCart(memberId, productId);
        return "移除成功";
    }
}
