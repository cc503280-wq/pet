package com.pet.dao.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.product.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	
	// ✅ 1. 查詢某人的購物車內，有沒有某個商品
    // 注意寫法：Member_MemberId 代表「去 Member 物件裡找 MemberId」
    // (假設您的 Member Entity ID 欄位叫 memberId，Product Entity ID 欄位叫 productId)
    Optional<CartItem> findByMember_MemberIdAndProduct_ProductId(Integer memberId, Integer productId);

    // ✅ 2. 查詢某人的「所有」購物車資料 (顯示購物車頁面用)
    List<CartItem> findByMember_MemberId(Integer memberId);
    
    // ✅ 3. 刪除某人的購物車內特定商品 (刪除功能用)
    void deleteByMember_MemberIdAndProduct_ProductId(Integer memberId, Integer productId);
}
