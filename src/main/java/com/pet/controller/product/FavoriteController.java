package com.pet.controller.product;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pet.model.product.Favorite;
import com.pet.service.product.FavoriteService;
import com.pet.util.LoginUser;

@RestController
@RequestMapping("/shop/favorites")
public class FavoriteController {

	@Autowired
    private FavoriteService favService;

    @GetMapping("/my")
    public ResponseEntity<List<Favorite>> getFavorites(@LoginUser Integer memberId) {
        return ResponseEntity.ok(favService.getMyFavorites(memberId));
    }

    // 🟢 新增：Toggle API
    // POST /shop/favorites/toggle
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFavorite(
    		@LoginUser Integer memberId, 
            @RequestBody Map<String, Integer> payload
    ) {
    	
    	if (memberId == null) {
            return ResponseEntity.status(401).body("請先登入");
        }
        Integer productId = payload.get("productId");

        if (memberId == null || productId == null) {
            return ResponseEntity.badRequest().body("缺少參數");
        }

        // 執行切換，並拿到最新的狀態 (true/false)
        boolean isFavorite = favService.toggleFavorite(memberId, productId);
        
        // 回傳 JSON 告訴前端現在是收藏還是取消
        return ResponseEntity.ok(Map.of(
            "status", isFavorite, 
            "message", isFavorite ? "已加入收藏" : "已取消收藏"
        ));
    }
}