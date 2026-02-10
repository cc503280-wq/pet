package com.pet.aspect;

// ===== Java 標準函式庫 =====
import java.time.LocalDateTime;    // 用於記錄操作的時間戳記
import java.util.HashMap;          // 用於建立 details Map
import java.util.Map;              // Map 介面
import java.util.Optional;         // 用於處理可能不存在的值

// ===== Spring AOP 相關 =====
import org.aspectj.lang.JoinPoint;           // 連接點：包含被攔截方法的資訊
import org.aspectj.lang.annotation.AfterReturning;  // AOP 通知類型：方法成功返回後執行
import org.aspectj.lang.annotation.Aspect;   // 標記這是一個切面類別

// ===== Spring 框架 =====
import org.springframework.http.ResponseEntity;  // HTTP 回應包裝類別
import org.springframework.stereotype.Component;  // 讓 Spring 管理這個 Bean

// ===== 專案內部 =====
import com.pet.annotation.LogAction;              // 我們自訂的註解
import com.pet.dto.common.ApiResponse;            // API 回應包裝 (修正這裡！)
import com.pet.dao.appointment.AppointmentRepository;  // 預約 Repository
import com.pet.dao.appointment.GroomerRepository;      // 美容師 Repository
import com.pet.dao.audit.GroomerActionLogRepository;  // MongoDB Repository
import com.pet.model.audit.GroomerActionLog;      // MongoDB 文件實體
import com.pet.model.appointment.Appointment;     // 預約實體
import com.pet.model.appointment.Groomer;         // 美容師實體

// ===== Servlet =====
import jakarta.servlet.http.HttpServletRequest;   // 用於取得 HTTP 請求資訊（IP、User-Agent）

// ===== Lombok =====
import lombok.RequiredArgsConstructor;  // 自動生成帶有 final 欄位的建構子
import lombok.extern.slf4j.Slf4j;       // 自動生成 log 物件

/**
 * 操作日誌切面 (Action Logging Aspect)
 * 
 * 這是一個 AOP 切面，用於自動記錄美容師的操作日誌到 MongoDB。
 * 
 * 工作原理：
 * 1. 當任何標記了 @LogAction 註解的方法被呼叫時
 * 2. Spring AOP 會在方法「成功返回後」攔截
 * 3. 自動提取相關資訊並儲存到 MongoDB
 * 
 * 優點：
 * - 不需要修改每個 Controller 的業務邏輯
 * - 統一管理日誌記錄的格式和儲存
 * - 日誌記錄失敗不會影響主流程
 */
@Aspect                    // 標記這是一個 AOP 切面類別
@Component                 // 讓 Spring 自動掃描並管理這個 Bean
@Slf4j                     // Lombok：自動生成 private static final Logger log = ...
@RequiredArgsConstructor   // Lombok：為所有 final 欄位生成建構子（用於依賴注入）
public class ActionLoggingAspect {

    // ===== 依賴注入 (透過建構子注入) =====
    
    // MongoDB Repository：用於將日誌儲存到 MongoDB
    private final GroomerActionLogRepository actionLogRepository;
    
    // 預約 Repository：用於查詢預約資訊（取得會員資料）
    private final AppointmentRepository appointmentRepository;
    
    // 美容師 Repository：用於查詢美容師姓名（當 Session 不可用時的備案）
    private final GroomerRepository groomerRepository;
    
    // HTTP 請求物件：用於取得客戶端 IP 和瀏覽器資訊
    // Spring 會自動注入當前請求的 HttpServletRequest
    private final HttpServletRequest request;

    /**
     * AOP 通知方法：在目標方法成功返回後執行
     * 
     * @AfterReturning 說明：
     * - pointcut = "@annotation(logAction)"
     *   → 攔截所有標記了 @LogAction 註解的方法
     *   → 這裡的 logAction 參數名稱要和方法參數對應
     * 
     * - returning = "result"
     *   → 取得目標方法的返回值，存入 result 參數
     *   → 這讓我們可以從返回值中提取美容師資訊
     * 
     * @param joinPoint  連接點，包含被攔截方法的所有資訊
     * @param logAction  目標方法上的 @LogAction 註解實例
     * @param result     目標方法的返回值
     */
    @AfterReturning(pointcut = "@annotation(logAction)", returning = "result")
    public void logGroomerAction(JoinPoint joinPoint, LogAction logAction, Object result) {
        try {
            // ===== 步驟 1：建立日誌物件 =====
            GroomerActionLog auditLog = new GroomerActionLog();
            
            // ===== 步驟 2：設定基本資訊 =====
            
            // 從 @LogAction 註解取得操作類型（如：LOGIN, LOGOUT, CHECK_IN）
            auditLog.setActionType(logAction.actionType());
            
            // 記錄當前時間
            auditLog.setTimestamp(LocalDateTime.now());
            
            // 記錄客戶端 IP 位址（用於追蹤來源）
            auditLog.setIpAddress(getClientIp());
            
            // 記錄瀏覽器資訊（User-Agent 標頭）
            // 例如：Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0
            auditLog.setUserAgent(request.getHeader("User-Agent"));
            
            // ===== 步驟 3：從返回值提取美容師資訊 =====
            // 因為 login() 方法會返回登入成功的 Groomer 物件
            // 我們從返回值中提取 groomerId 和 groomerName
            extractGroomerInfo(result, auditLog);
            
            // ===== 步驟 3.5：如果返回值沒有美容師資訊，嘗試從方法參數提取 =====
            // 這對於 logout、checkIn、complete 等方法很重要
            if (auditLog.getGroomerId() == null) {
                extractGroomerInfoFromArgs(joinPoint, auditLog);
            }
            
            // ===== 步驟 4：記錄額外詳情 =====
            // 使用 Map 儲存額外的彈性資料
            Map<String, Object> details = new HashMap<>();
            
            // 記錄註解中的描述文字
            details.put("description", logAction.description());
            
            // 記錄被呼叫的方法名稱
            // joinPoint.getSignature().getName() 會回傳如 "login"、"logout"
            details.put("method", joinPoint.getSignature().getName());
            
            // 嘗試記錄 appointmentId 和會員資訊（對於報到、完成操作）
            extractAppointmentAndMemberInfo(joinPoint, result, auditLog, details);
            
            // 🔧 備案：如果有 groomerId 但沒有 groomerName，從資料庫查詢
            // 這對於 LOGOUT 特別重要（因為跨域問題導致 Session 不可用）
            if (auditLog.getGroomerId() != null && auditLog.getGroomerName() == null) {
                lookupGroomerNameFromDatabase(auditLog);
            }
            
            auditLog.setDetails(details);
            
            // ===== 步驟 5：儲存到 MongoDB =====
            // 呼叫 Repository 的 save 方法，將日誌寫入 MongoDB
            actionLogRepository.save(auditLog);
            
            // 在 console 輸出成功訊息（方便開發時確認）
            log.info("✅ 操作日誌已記錄: {} - 美容師ID: {}", 
                logAction.actionType(), auditLog.getGroomerId());
                
        } catch (Exception e) {
            // ===== 重要：日誌記錄失敗不應該影響主流程 =====
            // 即使 MongoDB 掛了，也不應該讓登入功能失敗
            // 只記錄錯誤日誌，不拋出例外
            log.error("❌ 操作日誌記錄失敗", e);
        }
    }
    
    /**
     * 從方法參數中提取美容師資訊
     * 
     * 🛡️ 安全性改進：
     * - 對於 checkIn / complete 方法，改從 Session 取得美容師資訊（無法被前端偽造）
     * - 只有 logout 的 Map 參數仍然需要從參數提取（但只用於記錄，Session 會在之後被 invalidate）
     */
    @SuppressWarnings("unchecked")
    private void extractGroomerInfoFromArgs(JoinPoint joinPoint, GroomerActionLog auditLog) {
        String methodName = joinPoint.getSignature().getName();
        
        // 🛡️ 安全性改進：從 Session 取得美容師資訊（而非方法參數）
        // 適用於 checkIn / complete / logout / start 等需要記錄操作者的方法
        if (methodName.contains("checkIn") || methodName.contains("complete") || methodName.contains("logout") || methodName.contains("start")) {
            extractGroomerFromSession(auditLog);
            // 如果已從 Session 取得，直接返回
            if (auditLog.getGroomerId() != null) {
                return;
            }
        }
        
        // 備用方案：從 Map 參數中提取（例如 logout 的 payload，用於向後相容）
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) arg;
                
                if (map.containsKey("groomerId") && auditLog.getGroomerId() == null) {
                    Object groomerId = map.get("groomerId");
                    if (groomerId instanceof Integer) {
                        auditLog.setGroomerId((Integer) groomerId);
                    } else if (groomerId instanceof String) {
                        auditLog.setGroomerId(Integer.parseInt((String) groomerId));
                    } else if (groomerId instanceof Number) {
                        auditLog.setGroomerId(((Number) groomerId).intValue());
                    }
                }
                if (map.containsKey("groomerName") && auditLog.getGroomerName() == null) {
                    auditLog.setGroomerName((String) map.get("groomerName"));
                }
            }
        }
    }
    
    /**
     * 🛡️ 從 HttpSession 或 Request Attribute 取得當前登入的美容師資訊
     * 
     * 這比從前端傳遞參數更安全，因為 Session 是由後端控制的，
     * 無法被惡意使用者偽造。
     * 
     * 🔧 修正：對於 LOGOUT 操作，Session 在 Controller 方法中已被 invalidate，
     * 因此改從 Request Attribute 讀取（Controller 在 invalidate 前會存入）
     */
    private void extractGroomerFromSession(GroomerActionLog auditLog) {
        try {
            // 先嘗試從 Session 取得
            jakarta.servlet.http.HttpSession session = request.getSession(false);
            if (session != null) {
                Object groomerId = session.getAttribute("currentGroomerId");
                Object groomerName = session.getAttribute("currentGroomerName");
                
                if (groomerId instanceof Integer) {
                    auditLog.setGroomerId((Integer) groomerId);
                }
                if (groomerName instanceof String) {
                    auditLog.setGroomerName((String) groomerName);
                }
                
                if (auditLog.getGroomerId() != null) {
                    log.debug("從 Session 取得美容師資訊: ID={}, Name={}", 
                        auditLog.getGroomerId(), auditLog.getGroomerName());
                    return; // 成功從 Session 取得，直接返回
                }
            }
            
            // 🔧 如果 Session 沒有資料（可能已 invalidate），從 Request Attribute 取得
            // 這是專門為 LOGOUT 操作設計的備案
            Object logoutGroomerId = request.getAttribute("logoutGroomerId");
            Object logoutGroomerName = request.getAttribute("logoutGroomerName");
            
            if (logoutGroomerId instanceof Integer && auditLog.getGroomerId() == null) {
                auditLog.setGroomerId((Integer) logoutGroomerId);
            }
            if (logoutGroomerName instanceof String && auditLog.getGroomerName() == null) {
                auditLog.setGroomerName((String) logoutGroomerName);
            }
            
            if (auditLog.getGroomerId() != null) {
                log.debug("🔧 從 Request Attribute 取得美容師資訊 (LOGOUT): ID={}, Name={}", 
                    auditLog.getGroomerId(), auditLog.getGroomerName());
            }
        } catch (Exception e) {
            log.warn("無法從 Session/Request 取得美容師資訊: {}", e.getMessage());
        }
    }
    
    /**
     * 🔧 從資料庫查詢美容師姓名（備案）
     * 
     * 當有 groomerId 但沒有 groomerName 時使用。
     * 主要用於 LOGOUT 操作（因為跨域問題導致 Session 不可用）
     */
    private void lookupGroomerNameFromDatabase(GroomerActionLog auditLog) {
        try {
            Optional<Groomer> groomerOpt = groomerRepository.findById(auditLog.getGroomerId());
            if (groomerOpt.isPresent()) {
                String groomerName = groomerOpt.get().getGroomerName();
                auditLog.setGroomerName(groomerName);
                log.debug("🔧 從資料庫查詢美容師姓名: ID={}, Name={}", 
                    auditLog.getGroomerId(), groomerName);
            }
        } catch (Exception e) {
            log.warn("無法從資料庫查詢美容師姓名: {}", e.getMessage());
        }
    }
    
    /**
     * 從 method 參數 與 回傳值 中提取資訊
     * 
     * ⚡ 效能優化策略：
     * 1. 優先嘗試從 result (ApiResponse) 中直接取得 Appointment 物件
     * 2. 如果回傳值中沒有資料，才降級使用 appointmentRepository.findById() 查詢 DB
     */
    private void extractAppointmentAndMemberInfo(JoinPoint joinPoint, Object result, GroomerActionLog auditLog, Map<String, Object> details) {
        String methodName = joinPoint.getSignature().getName();
        
        // 只對 checkIn、complete、start 方法提取
        if (!methodName.contains("checkIn") && !methodName.contains("complete") && !methodName.contains("start")) {
            return;
        }
        
        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || !(args[0] instanceof Integer)) {
            return;
        }
        
        Integer appointmentId = (Integer) args[0];
        auditLog.setTargetAppointmentId(appointmentId);
        details.put("appointmentId", appointmentId);
        
        // ⚡ 策略 1: 嘗試從回傳值 (result) 中取得 Appointment 資料 (省一次 DB 查詢)
        if (extractFromApiResponse(result, auditLog, details)) {
            log.debug("⚡ 已從 ApiResponse 取得預約資訊 (無需查 DB)");
            return; 
        }
        
        // ⚡ 策略 2: 如果回傳值沒資料，才查 DB (備案)
        log.debug("⚠️ 回傳值無預約資料，降級為 DB 查詢: {}", appointmentId);
        extractFromDatabase(appointmentId, auditLog, details);
    }
    
    /**
     * 嘗試從 ApiResponse 中提取 Appointment 資訊
     * @return boolean 是否成功提取
     */
    private boolean extractFromApiResponse(Object result, GroomerActionLog auditLog, Map<String, Object> details) {
        try {
            if (result instanceof ResponseEntity) {
                Object body = ((ResponseEntity<?>) result).getBody();
                if (body instanceof ApiResponse) {
                    Object data = ((ApiResponse<?>) body).getData();
                    if (data instanceof Appointment) {
                        Appointment apt = (Appointment) data;
                        fillMemberInfo(apt, auditLog, details);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("無法從 ApiResponse 提取資訊: {}", e.getMessage());
        }
        return false;
    }
    
    /**
     * 從資料庫查詢 Appointment 資訊
     */
    private void extractFromDatabase(Integer appointmentId, GroomerActionLog auditLog, Map<String, Object> details) {
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isPresent()) {
                fillMemberInfo(appointmentOpt.get(), auditLog, details);
            }
        } catch (Exception e) {
            log.warn("無法查詢預約資訊: {}", e.getMessage());
        }
    }
    
    /**
     * 共用邏輯：將 Appointment 中的會員與美容師資訊填入 Log
     * 
     * 🔧 修正：對於 CHECK_IN / COMPLETE 等「會員端」API，Session 中沒有美容師資訊，
     * 故改從 Appointment.groomer 關聯取得該筆預約的美容師資料。
     */
    private void fillMemberInfo(Appointment appointment, GroomerActionLog auditLog, Map<String, Object> details) {
        // 從預約中取得會員資訊
        if (appointment.getMemberPet() != null && 
            appointment.getMemberPet().getMember() != null) {
            
            var member = appointment.getMemberPet().getMember();
            auditLog.setTargetMemberId(member.getMemberId());
            auditLog.setTargetMemberName(member.getName());
            
            details.put("memberName", member.getName());
            details.put("memberId", member.getMemberId());
        }
        
        // 也記錄寵物資訊
        if (appointment.getMemberPet() != null) {
            details.put("petId", appointment.getMemberPet().getPetId());
            details.put("petName", appointment.getMemberPet().getPetName());
        }
        
        // 🔧 修正：如果 auditLog 還沒有美容師資訊，從 Appointment.groomer 取得
        // 這對於 CHECK_IN / COMPLETE (會員端 API) 很重要，因為這些操作時 Session 沒有美容師資訊
        if (auditLog.getGroomerId() == null && appointment.getGroomer() != null) {
            Groomer groomer = appointment.getGroomer();
            auditLog.setGroomerId(groomer.getGroomerId());
            auditLog.setGroomerName(groomer.getGroomerName());
            log.debug("🔧 從 Appointment.groomer 取得美容師資訊: ID={}, Name={}", 
                groomer.getGroomerId(), groomer.getGroomerName());
        }
    }
    
    /**
     * 從 Controller 的返回值中提取美容師資訊
     * 
     * 因為不同的 Controller 方法可能返回不同格式：
     * 1. ResponseEntity<Groomer> → 直接返回 Groomer 物件
     * 2. ResponseEntity<Map<String, Object>> → 返回包含 groomerId 的 Map
     * 
     * 這個方法會嘗試從這些格式中提取美容師資訊
     * 
     * @param result    Controller 方法的返回值
     * @param auditLog  要填入資訊的日誌物件
     */
    private void extractGroomerInfo(Object result, GroomerActionLog auditLog) {
        // 檢查返回值是否為 ResponseEntity 類型
        if (result instanceof ResponseEntity) {
            // 取得 ResponseEntity 的 body（實際回傳的資料）
            Object body = ((ResponseEntity<?>) result).getBody();
            
            // 情況 1：body 是 Groomer 物件
            if (body instanceof Groomer) {
                Groomer groomer = (Groomer) body;
                // 提取美容師 ID
                auditLog.setGroomerId(groomer.getGroomerId());
                // 提取美容師姓名
                auditLog.setGroomerName(groomer.getGroomerName());
            } 
            // 情況 2：body 是 Map（如 {"groomerId": 5, "groomerName": "小美"}）
            else if (body instanceof Map) {
                // 型別轉換，並忽略 unchecked 警告
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) body;
                
                // 檢查 Map 中是否有 groomerId
                if (map.containsKey("groomerId")) {
                    auditLog.setGroomerId((Integer) map.get("groomerId"));
                }
                // 檢查 Map 中是否有 groomerName
                if (map.containsKey("groomerName")) {
                    auditLog.setGroomerName((String) map.get("groomerName"));
                }
            }
        }
    }
    
    /**
     * 取得客戶端的真實 IP 位址
     * 
     * 為什麼需要檢查多個 Header？
     * - 如果用戶透過反向代理（如 Nginx）連線，request.getRemoteAddr() 會是代理的 IP
     * - 反向代理通常會把真實客戶端 IP 放在 X-Forwarded-For 或 X-Real-IP 標頭
     * 
     * 優先順序：
     * 1. X-Forwarded-For（最常見的代理標頭）
     * 2. X-Real-IP（Nginx 常用的標頭）
     * 3. request.getRemoteAddr()（直連時的 IP）
     * 
     * @return 客戶端 IP 位址
     */
    private String getClientIp() {
        // 嘗試從 X-Forwarded-For 標頭取得（多個代理時會有逗號分隔的 IP 列表）
        String ip = request.getHeader("X-Forwarded-For");
        
        // 如果沒有或是 "unknown"，嘗試 X-Real-IP
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        
        // 如果還是沒有，使用 getRemoteAddr()（直接連線的 IP）
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        return ip;
    }
}