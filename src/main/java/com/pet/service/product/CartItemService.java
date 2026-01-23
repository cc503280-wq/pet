package com.pet.service.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dao.member.MemberRepository;
import com.pet.dao.product.CartItemRepository;
import com.pet.dao.product.CartItemResponse;
import com.pet.dao.product.ProductRepository;
import com.pet.model.member.Member;
import com.pet.model.product.CartItem;
import com.pet.model.product.Product;

@Service
@Transactional
public class CartItemService {
	
	@Autowired
    private CartItemRepository cartRepos;

    @Autowired
    private ProductRepository productRepos;

    @Autowired
    private MemberRepository memberRepos;

    /**
     * 🔥 核心功能：加入購物車
     * 邏輯：如果車裡已經有，就加數量；如果沒有，就新增一筆。
     */
    public void addToCart(Integer memberId, Integer productId, Integer quantity) {
        // 1. 檢查商品是否存在 (防止 ID 亂傳)
        Product product = productRepos.findById(productId)
                .orElseThrow(() -> new RuntimeException("找不到商品 ID: " + productId));

        // 2. 查詢該會員的購物車中，是否已有此商品
        Optional<CartItem> existingItemOpt = cartRepos.findByMember_MemberIdAndProduct_ProductId(memberId, productId);

        if (existingItemOpt.isPresent()) {
            // A. 【已存在】：更新數量
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
            
            // (選用) 可以在這裡檢查庫存夠不夠
            // if (newQuantity > product.getStock()) { throw ... }

            existingItem.setQuantity(newQuantity);
            
            // JPA 有 Dirty Checking 機制，其實這裡不 call save 也會更新，但寫出來比較明確
            cartRepos.save(existingItem);
            System.out.println("✅ 商品已存在，數量更新為: " + newQuantity);

        } else {
            // B. 【不存在】：新增一筆
            Member member = memberRepos.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("找不到會員 ID: " + memberId));

            CartItem newItem = new CartItem();
            newItem.setMember(member);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            
            // 如果您有保留 price_at_added 欄位，可以在這裡設值
            // newItem.setPriceAtAdded(product.getPrice());

            cartRepos.save(newItem);
            System.out.println("✅ 新增購物車項目: " + product.getProductName());
        }
    }

    /**
     * 📋 查詢某人的購物車清單
     */
public List<CartItemResponse> getMyCart(Integer memberId) {
        
        // 🔥 1. 把嚴謹檢查搬進來
        if (memberId == null) {
            throw new IllegalArgumentException("會員 ID 不能為空");
        }
        
        boolean exists = memberRepos.existsById(memberId);
        if (!exists) {
            // 建議拋出自定義異常，讓全域異常處理器捕捉
            throw new RuntimeException("查無此會員"); 
        }

        // 2. 撈資料
        List<CartItem> cartItems = cartRepos.findByMember_MemberId(memberId);
        
        // 3. 轉換 DTO (在這裡做最安全)
        List<CartItemResponse> responseList = new ArrayList<>();
        
        for (CartItem item : cartItems) {
            CartItemResponse dto = new CartItemResponse();
            dto.setCartItemId(item.getCartItemId());
            dto.setProductId(item.getProduct().getProductId());
            dto.setProductName(item.getProduct().getProductName());
            dto.setImageUrl(item.getProduct().getImageUrl());
            dto.setPrice(item.getProduct().getPrice());
            dto.setQuantity(item.getQuantity());
            dto.setStock(item.getProduct().getStock());
            
            // 計算小計的邏輯放在 Service 是最正確的
            dto.setSubtotal(item.getProduct().getPrice() * item.getQuantity());
            
            responseList.add(dto);
        }
        
        return responseList;
    }

    /**
     * ✏️ 更新購物車數量 (直接指定數量，例如在購物車頁面改數字)
     */
    public void updateQuantity(Integer memberId, Integer productId, Integer newQuantity) {
        CartItem item = cartRepos.findByMember_MemberIdAndProduct_ProductId(memberId, productId)
                .orElseThrow(() -> new RuntimeException("購物車找不到該商品"));
        
        if (newQuantity <= 0) {
            // 如果數量改為 0 或負數，視為刪除
            cartRepos.delete(item);
        } else {
            item.setQuantity(newQuantity);
            cartRepos.save(item);
        }
    }

    /**
     * 🗑️ 移除購物車項目
     */
    public void removeFromCart(Integer memberId, Integer productId) {
        cartRepos.deleteByMember_MemberIdAndProduct_ProductId(memberId, productId);
    }
    
    /**
     * 🧹 清空購物車 (結帳後用)
     */
    public void clearCart(Integer memberId) {
        // 這裡通常會用 SQL delete where member_id = ? 比較快
        List<CartItem> items = cartRepos.findByMember_MemberId(memberId);
        cartRepos.deleteAll(items);
    }

}
