package com.pet.dao.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pet.model.product.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    
    // 查詢某人的所有收藏 (最新的在上面)
    List<Favorite> findByMemberIdOrderByFavoriteIdDesc(Integer memberId);

    // 檢查是否已收藏 (避免重複)
    boolean existsByMemberIdAndProduct_ProductId(Integer memberId, Integer productId);

    // 刪除收藏
    void deleteByMemberIdAndProduct_ProductId(Integer memberId, Integer productId);
    
    Optional<Favorite> findByMemberIdAndProduct_ProductId(Integer memberId, Integer productId);
}