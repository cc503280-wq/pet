package com.pet.service.member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dao.member.CouponRepository;
import com.pet.dao.member.CouponUserRecordRepository;
import com.pet.model.member.Coupon;
import com.pet.model.member.CouponUserRecord;


@Service
@Transactional
public class CouponService {

	@Autowired
	private CouponRepository couponRepository;
	
	@Autowired
	private CouponUserRecordRepository couponUserRecordRepository;
	
	public List<Coupon> getAllCoupons() {
        return couponRepository.findAllByOrderByCouponIdAsc();
    }

    public List<Coupon> getCouponsByStatus(String status) {
        return couponRepository.findByStatusOrderByCouponIdAsc(status);
    }

    public Coupon getCouponById(Integer id) {
        return couponRepository.findById(id).orElse(null);
    }

    public List<Coupon> getCouponsByIssueRange(LocalDate start, LocalDate end) {
        return couponRepository.findByIssueRange(start, end);
    }

    public List<Coupon> getCouponsByUseRange(LocalDate start, LocalDate end) {
        return couponRepository.findByUseRange(start, end);
    }

    public Coupon createCoupon(Coupon coupon) {
        validateCoupon(coupon);
        return couponRepository.save(coupon);
    }

    public Coupon updateCoupon(Coupon input) {
        validateCoupon(input);

        Optional<Coupon> optional = couponRepository.findById(input.getCouponId());

        if (optional.isPresent()) {
            Coupon coupon = optional.get();

            coupon.setCode(input.getCode());
            coupon.setDiscountType(input.getDiscountType());
            coupon.setDiscountValue(input.getDiscountValue());
            coupon.setIsLimited(input.getIsLimited());
            coupon.setTotalAmount(input.getTotalAmount());
            coupon.setIssuedAmount(input.getIssuedAmount());
            coupon.setIssueStartAt(input.getIssueStartAt());
            coupon.setIssueEndAt(input.getIssueEndAt());
            coupon.setUseStartAt(input.getUseStartAt());
            coupon.setUseEndAt(input.getUseEndAt());
            coupon.setMinPurchase(input.getMinPurchase());

            return couponRepository.save(coupon);
        } else {
            return null;
        }
    }

    public boolean toggleStatus(Integer id) {
        Optional<Coupon> optional = couponRepository.findById(id);

        if (optional.isPresent()) {
            Coupon coupon = optional.get();
            
            if ("active".equals(coupon.getStatus())) {
                coupon.setStatus("disabled");
            } else {
                coupon.setStatus("active");
            }
            
            return true;
        } else {
            return false;
        }
    }
    
    private void validateCoupon(Coupon c) {
        // 1. 折扣類型與數值驗證
        if ("percent".equals(c.getDiscountType())) {
            if (c.getDiscountValue() == null || c.getDiscountValue() < 0.0 || c.getDiscountValue() > 1.0) {
                throw new IllegalArgumentException("百分比折扣必須介於 0.0 ~ 1.0（例如 0.5 代表五折）");
            }
        } else if ("amount".equals(c.getDiscountType())) {
            if (c.getDiscountValue() == null || c.getDiscountValue() < 0) {
                throw new IllegalArgumentException("金額折扣不得小於 0");
            }
            if (c.getMinPurchase() != null && c.getDiscountValue() > c.getMinPurchase()) {
                throw new IllegalArgumentException("折抵金額不可超過最低消費金額");
            }
        }

        // 2. 數值欄位負數驗證 (使用 Optional 處理可能為 null 的 Integer)
        if (isNegative(c.getIsLimited()) || isNegative(c.getMinPurchase()) || 
            isNegative(c.getTotalAmount()) || isNegative(c.getIssuedAmount())) {
            throw new IllegalArgumentException("數值欄位（限量、金額、門檻）不能小於 0");
        }

        // 3. 日期邏輯驗證 (使用 LocalDate)
        LocalDate issueS = c.getIssueStartAt();
        LocalDate issueE = c.getIssueEndAt();
        LocalDate useS = c.getUseStartAt();
        LocalDate useE = c.getUseEndAt();

        if (issueS == null || issueE == null || useS == null || useE == null) {
            throw new IllegalArgumentException("所有日期欄位均為必填");
        }

        if (issueS.isAfter(issueE)) {
            throw new IllegalArgumentException("發放開始日期 (" + issueS + ") 不得晚於發放結束日期 (" + issueE + ")");
        }

        if (useS.isAfter(useE)) {
            throw new IllegalArgumentException("使用開始日期 (" + useS + ") 不得晚於使用結束日期 (" + useE + ")");
        }

        if (useS.isBefore(issueS)) {
            throw new IllegalArgumentException("使用開始日期 (" + useS + ") 不得早於發放開始日期 (" + issueS + ")");
        }
        
        if (useE.isBefore(issueE)) {
            throw new IllegalArgumentException("使用結束日期不可早於發放結束日期");
        }
    }

    private boolean isNegative(Integer val) {
        return val != null && val < 0;
    }
    
    //領取優惠券核心邏輯
    public void claimCoupon(Integer userId, Integer couponId) {
        // 1. 取得優惠券規格，找不到則拋出異常
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("該優惠券不存在"));

        // 2. 驗證：是否為啟用狀態
        if (!"active".equals(coupon.getStatus())) {
            throw new RuntimeException("此優惠券目前無法領取");
        }

        // 3. 驗證：是否在發放期間內
        LocalDate now = LocalDate.now();
        if (now.isBefore(coupon.getIssueStartAt()) || now.isAfter(coupon.getIssueEndAt())) {
            throw new RuntimeException("目前非領取時間 (發放期間: " + 
                                        coupon.getIssueStartAt() + " ~ " + coupon.getIssueEndAt() + ")");
        }

        // 4. 驗證：重複領取檢查 (使用你剛寫好的existsBy方法)
        if (couponUserRecordRepository.existsByMemberIdAndCouponId(userId, couponId)) {
            throw new RuntimeException("您已經領取過此優惠券囉！");
        }

        // 5. 驗證：限量檢查
        if (coupon.getIsLimited() != null && coupon.getIsLimited() == 1) {
            if (coupon.getIssuedAmount() != null && coupon.getIssuedAmount() >= coupon.getTotalAmount()) {
                throw new RuntimeException("好可惜！優惠券已被領取完畢");
            }
        }

        // 6. 執行領取 A：增加 Coupon 的已發放數量 (issuedAmount)
        Integer currentIssued = (coupon.getIssuedAmount() != null) ? coupon.getIssuedAmount() : 0;
        coupon.setIssuedAmount(currentIssued + 1);
        couponRepository.save(coupon);

        // 7. 執行領取 B：新增紀錄到 coupon_user 表
        CouponUserRecord record = CouponUserRecord.builder()
                .memberId(userId)
                .couponId(couponId)
                .status("unused") // 初始狀態為未使用
                .assignedAt(LocalDateTime.now()) // 領取時間
                .build();
        
        couponUserRecordRepository.save(record);
    }
    
    //供前端領取中心使用，只抓「活著」的券
    public List<Coupon> getAvailableCoupons() {
        return couponRepository.findAvailableCoupons(LocalDate.now());
    }
}    
