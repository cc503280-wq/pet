package com.pet.aspect;

import com.pet.dao.member.MemberActionLogRepository;
import com.pet.model.member.MemberActionLog;
import com.pet.model.order.Order;
import com.pet.model.appointment.AppointmentRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Add Import
import org.springframework.security.core.context.SecurityContextHolder; // Add Import
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * AOP 切面程式：攔截 @LogAction 並儲存紀錄
 */
@Aspect // 宣告這是一個「切面」類別，專門用來處理跨多個模組的共用邏輯（如日誌、權限、交易）
@Component
@Slf4j // Lombok 提供的功能，讓你直接在程式碼裡寫 log.info()
public class MemberActionAspect {

    @Autowired
    private MemberActionLogRepository logRepository;
    @Autowired
    private com.pet.dao.product.CategoryRepository categoryRepository;

    /**
     * 後置通知 (AfterReturning)：只有方法執行成功才紀錄
     * 攔截所有標註 @LogAction 的方法
     */
    @AfterReturning(pointcut = "@annotation(logAction)", returning = "result")
    public void logMemberAction(JoinPoint joinPoint, LogAction logAction, Object result) {
        try {
            // 1. 取得動作類型
            String actionType = logAction.type();

            // 2. 嘗試取得當前請求的 Request 與 Session (為了撈 IP 和 MemberId)
            HttpServletRequest request = null;
            HttpSession session = null;
            Integer memberId = null;
            String clientIp = "unknown";

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
                session = request.getSession(false);
                clientIp = getClientIp(request);

                // 1. 嘗試從 SecurityContext (JWT) 取得 UserID
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof Integer) {
                    memberId = (Integer) auth.getPrincipal();
                }

                // 2. 如果沒抓到，嘗試從 Session 抓取 (傳統 Web 登入)
                if (memberId == null && session != null && session.getAttribute("memberId") != null) {
                    memberId = (Integer) session.getAttribute("memberId");
                }

            }

            // 參數解析變數
            Object[] args = joinPoint.getArgs();
            String targetId = null;
            String detail = null;

            // 3. 根據不同行為類型，解析特定參數
            switch (actionType) {
                case LogAction.ActionType.SEARCH:
                    // getStoreProducts(..., categoryId, keyword, ...) -> [2]categoryId, [3]keyword
                    if (args.length >= 4) {
                        String keyword = (args[3] instanceof String) ? (String) args[3] : null;
                        Integer categoryId = (args[2] instanceof Integer) ? (Integer) args[2] : null;

                        StringBuilder sb = new StringBuilder();
                        if (keyword != null && !keyword.isBlank())
                            sb.append("關鍵字: ").append(keyword).append(" ");

                        if (categoryId != null) {
                            String categoryName = categoryRepository.findById(categoryId)
                                    .map(c -> c.getCategoryName())
                                    .orElse(String.valueOf(categoryId));
                            sb.append("分類: ").append(categoryName);
                        }

                        detail = sb.toString().trim();
                        if (detail.isEmpty()) {
                            return; // 只有單純瀏覽列表（無關鍵字、無分類）時，不進行紀錄
                        }
                    }
                    // 兼容舊版 searchProducts(String keyword) -> args[0]
                    else if (args.length > 0 && args[0] instanceof String) {
                        detail = "搜尋關鍵字: " + args[0];
                    }
                    break;

                case LogAction.ActionType.FAVORITE:
                    // toggleFavorite(Integer memberId, Integer productId) -> return boolean
                    if (args.length >= 2) {
                        if (args[0] instanceof Integer)
                            memberId = (Integer) args[0];
                        targetId = String.valueOf(args[1]); // productId

                        // 判斷是加入還是取消
                        if (result instanceof Boolean) {
                            detail = ((Boolean) result) ? "加入收藏" : "取消收藏";
                        } else {
                            detail = "收藏操作";
                        }
                    }
                    break;

                case LogAction.ActionType.CART:
                    // 根據方法名稱判斷是「加入」還是「移除」
                    String methodName = joinPoint.getSignature().getName();

                    if ("addToCart".equals(methodName)) {
                        // addToCart(Integer memberId, Integer productId, Integer quantity)
                        if (args.length >= 3) {
                            if (args[0] instanceof Integer)
                                memberId = (Integer) args[0];
                            targetId = String.valueOf(args[1]); // productId
                            detail = "加入購物車數量: " + args[2];
                        }
                    } else if ("removeFromCart".equals(methodName)) {
                        // removeFromCart(Integer memberId, Integer productId)
                        if (args.length >= 2) {
                            if (args[0] instanceof Integer)
                                memberId = (Integer) args[0];
                            targetId = String.valueOf(args[1]); // productId
                            detail = "移除購物車商品";
                        }
                    }
                    break;

                case LogAction.ActionType.CREATE_ORDER:
                    // insertOrder(Order order) -> order 物件裡有 memberId, orderId, totalAmount
                    if (args.length > 0 && args[0] instanceof Order) {
                        Order order = (Order) args[0];
                        memberId = order.getMemberId();
                        targetId = String.valueOf(order.getOrderId()); // 注意: 插入前可能沒 ID
                        // 如果回傳值 result 是 Order (且已存檔有 ID)，改用 result 抓
                        if (result instanceof Order) {
                            targetId = String.valueOf(((Order) result).getOrderId());
                            memberId = ((Order) result).getMemberId();
                        }
                        detail = "訂單金額: " + order.getTotalAmountDiscountPoints();
                    }
                    break;

                case LogAction.ActionType.BOOKING:
                    // saveAppointment(AppointmentRequest request)
                    if (args.length > 0 && args[0] instanceof AppointmentRequest) {
                        AppointmentRequest req = (AppointmentRequest) args[0];
                        memberId = req.getMemberId();
                        targetId = String.valueOf(req.getPetId()); // 暫存 PetID
                        detail = "預約日期: " + req.getAppointmentDate() + " " + req.getStartTime();

                        // 如果有回傳 Appointment 實體，改抓 Appointment ID
                        // (假設 Service 回傳 Appointment)
                        // if (result instanceof Appointment) { ... }
                    }
                    break;

                case LogAction.ActionType.LOGIN:
                    // 通常登入 Controller 會回傳 Member 或 Token
                    // 這裡先保留，稍後在 OAuth2Handler 手動呼叫比較準
                    break;
            }

            // 4. 如果到最後還是沒抓到 MemberId (例如 Search 時未登入)，就不紀錄或是記為 0 (訪客)
            if (memberId == null) {
                // 如果是 SEARCH 行為，且沒登入，我們記錄為 0 (訪客) 以便分析關鍵字
                if (LogAction.ActionType.SEARCH.equals(actionType)) {
                    memberId = 0;
                } else {
                    // 其他行為 (如下單、預約) 必須要登入才紀錄
                    return;
                }
            }

            // 5. 存入資料庫
            MemberActionLog logEntry = MemberActionLog.builder()
                    .memberId(memberId)
                    .actionType(actionType)
                    .targetId(targetId)
                    .detail(detail)
                    .clientIp(clientIp)
                    .actionTime(LocalDateTime.now())
                    .build();

            logRepository.save(logEntry);
            log.info("已紀錄會員行為: [Member: {}] [Action: {}] [Detail: {}]", memberId, actionType, detail);

        } catch (Exception e) {
            log.error("AOP 紀錄會員行為失敗", e);
            // 不拋出異常，避免影響主流程
        }
    }

    /**
     * 輔助方法：取得客戶端真實 IP (考慮 Proxy)
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
