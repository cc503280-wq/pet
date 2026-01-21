package com.pet.service.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dao.product.FavoriteRepository;
import com.pet.dao.product.ProductRepository;
import com.pet.model.product.Favorite;
import com.pet.model.product.Product;

@Service
@Transactional
public class FavoriteService {

	@Autowired
    private FavoriteRepository favRepo;
    @Autowired
    private ProductRepository productRepo;

    // 取得列表 (保持不變)
    public List<Favorite> getMyFavorites(Integer memberId) {
        return favRepo.findByMemberIdOrderByFavoriteIdDesc(memberId);
    }

    // 🟢 重寫：Toggle 功能 (回傳 boolean: true=已收藏, false=已取消)
    public boolean toggleFavorite(Integer memberId, Integer productId) {
        // 1. 先試著找找看這筆收藏存不存在
        Optional<Favorite> existingFav = favRepo.findByMemberIdAndProduct_ProductId(memberId, productId);

        if (existingFav.isPresent()) {
            // A. 如果存在 -> 執行刪除
            favRepo.delete(existingFav.get());
            return false; // 回傳 false 代表現在「沒有」收藏了
        } else {
            // B. 如果不存在 -> 執行新增 (Save)
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("商品不存在"));

            Favorite fav = new Favorite();
            fav.setMemberId(memberId);
            fav.setProduct(product); // 這裡設定關聯，JPA 會自動存入 product_id
            
            favRepo.save(fav); // 🟢 這裡確保執行 Save
            return true; // 回傳 true 代表現在「有」收藏了
        }
    }
}