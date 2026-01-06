package com.pet.controller.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.member.Favorites;
import com.pet.service.member.FavoritesService;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

	@Autowired
	private FavoritesService fService;
	
	// 全部：/favorites
    @GetMapping
    public List<Favorites> listAll() {
        return fService.getAllFavorites();
    }

    // 依會員：/favorites/member/1
    @GetMapping("/member/{memberId}")
    public ResponseEntity<?> queryByMember(@PathVariable Integer memberId) {
        try {
            List<Favorites> list = fService.getFavoritesByMemberId(memberId);
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // 依商品：/favorites/product/1
    @GetMapping("/product/{productId}")
    public List<Favorites> queryByProduct(@PathVariable Integer productId) {
        return fService.getFavoritesByProductId(productId);
    }
}
