package com.pet.aspect;

// ===== Java 標準函式庫 =====
import java.time.LocalDateTime;              // 用於記錄時間戳記
import java.util.concurrent.CompletableFuture;  // Java 8+ 非同步處理工具

// ===== Spring AOP 相關 =====
import org.aspectj.lang.ProceedingJoinPoint;  // @Around 專用的連接點，可以控制方法是否執行
import org.aspectj.lang.annotation.Around;    // 環繞通知：可在方法前後都執行邏輯
import org.aspectj.lang.annotation.Aspect;    // 標記這是一個切面類別

// ===== Spring 框架 =====
import org.springframework.stereotype.Component;  // 讓 Spring 管理這個 Bean

// ===== 專案內部 =====
import com.pet.dao.audit.PerformanceMetricRepository;  // MongoDB Repository
import com.pet.model.audit.PerformanceMetric;          // MongoDB 文件實體

// ===== Lombok =====
import lombok.RequiredArgsConstructor;  // 自動生成帶有 final 欄位的建構子
import lombok.extern.slf4j.Slf4j;       // 自動生成 log 物件

/**
 * 效能監控切面 (Performance Monitor Aspect)
 * 
 * 這個切面會自動監控 com.pet.service.appointment 套件下「所有方法」的執行時間，
 * 並將效能數據儲存到 MongoDB，用於後續分析系統瓶頸。
 * 
 * 監控範圍包括：
 * - AppointmentService（預約服務）
 * - GroomerService（美容師服務）
 * - DailyScheduleService（每日班表服務）
 * - ServiceItemService（服務項目服務）
 * 
 * 為什麼使用 @Around？
 * - @Around 是最強大的通知類型
 * - 可以在方法「執行前」和「執行後」都插入邏輯
 * - 可以控制是否執行原方法（透過 joinPoint.proceed()）
 * - 可以精確計算方法執行時間
 */
@Aspect                    // 標記這是一個 AOP 切面類別
@Component                 // 讓 Spring 自動掃描並管理這個 Bean
@Slf4j                     // Lombok：自動生成 private static final Logger log = ...
@RequiredArgsConstructor   // Lombok：為所有 final 欄位生成建構子（用於依賴注入）
public class PerformanceMonitorAspect {

    // ===== 依賴注入 =====
    
    // MongoDB Repository：用於將效能數據儲存到 MongoDB
    private final PerformanceMetricRepository metricRepository;

    /**
     * 環繞通知：監控 appointment 套件下所有方法的執行時間
     * 
     * @Around 說明：
     * - execution(* com.pet.service.appointment..*(..))
     *   │         │ │                        │ │ └── (..) 任意參數
     *   │         │ │                        │ └──── * 任意方法名
     *   │         │ │                        └────── * 任意類別
     *   │         │ └─────────────────────────────── .. 包含子套件
     *   │         └───────────────────────────────── * 任意返回類型
     *   └──────────────────────────────────────────── execution 表示方法執行時
     * 
     * 這個 Pointcut 會攔截：
     * - AppointmentService.saveAppointment()
     * - AppointmentService.cancelAppointment()
     * - GroomerService.groomerLogin()
     * - DailyScheduleService.getAvailableSlots()
     * - ... 等等 appointment 套件下的所有方法
     * 
     * @param joinPoint  ProceedingJoinPoint 連接點，可以呼叫 proceed() 執行原方法
     * @return           原方法的返回值
     * @throws Throwable 原方法可能拋出的任何例外
     */
    @Around("execution(* com.pet.service.appointment..*(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        
        // ===== 步驟 1：記錄開始時間 =====
        // System.currentTimeMillis() 返回從 1970 年 1 月 1 日到現在的毫秒數
        long startTime = System.currentTimeMillis();
        
        // 預設執行成功
        boolean success = true;
        
        // 錯誤訊息（若有）
        String errorMessage = null;
        
        try {
            // ===== 步驟 2：執行原本的方法 =====
            // joinPoint.proceed() 會呼叫被攔截的原方法
            // 如果不呼叫 proceed()，原方法就不會執行（可用於權限控制等場景）
            return joinPoint.proceed();
            
        } catch (Throwable e) {
            // ===== 步驟 3：捕捉例外 =====
            // 如果方法執行過程中拋出例外
            success = false;                 // 標記執行失敗
            errorMessage = e.getMessage();   // 記錄錯誤訊息
            
            // 重新拋出例外，不影響原本的錯誤處理流程
            // 這很重要！如果不拋出，呼叫者會以為方法成功了
            throw e;
            
        } finally {
            // ===== 步驟 4：計算執行時間（無論成功或失敗都會執行） =====
            // 結束時間 - 開始時間 = 執行時間（毫秒）
            long executionTime = System.currentTimeMillis() - startTime;
            
            // ===== 步驟 5：建立效能記錄物件 =====
            PerformanceMetric metric = new PerformanceMetric();
            
            // 取得類別名稱（不含套件路徑）
            // 例如：AppointmentService（而不是 com.pet.service.appointment.AppointmentService）
            metric.setClassName(joinPoint.getTarget().getClass().getSimpleName());
            
            // 取得方法名稱
            // 例如：saveAppointment
            metric.setMethodName(joinPoint.getSignature().getName());
            
            // 取得完整的方法簽名
            // 例如：public Appointment com.pet.service.appointment.AppointmentService.saveAppointment(...)
            metric.setMethodSignature(joinPoint.getSignature().toLongString());
            
            // 記錄當前時間
            metric.setTimestamp(LocalDateTime.now());
            
            // 記錄執行時間（毫秒）
            metric.setExecutionTimeMs(executionTime);
            
            // 記錄是否成功
            metric.setSuccess(success);
            
            // 記錄錯誤訊息（若有）
            metric.setErrorMessage(errorMessage);
            
            // ===== 步驟 6：非同步儲存到 MongoDB =====
            // 為什麼用非同步？
            // - 儲存日誌不應該影響主流程的回應時間
            // - 即使 MongoDB 很慢，用戶也不會感受到延遲
            // 
            // CompletableFuture.runAsync() 會在另一個執行緒中執行
            CompletableFuture.runAsync(() -> {
                try {
                    // 儲存到 MongoDB
                    metricRepository.save(metric);
                } catch (Exception e) {
                    // 儲存失敗只記錄錯誤，不影響主流程
                    log.error("效能記錄儲存失敗", e);
                }
            });
            
            // ===== 步驟 7：效能警告 =====
            // 如果方法執行時間超過 1 秒（1000 毫秒），記錄警告
            // 這有助於快速發現效能問題
            if (executionTime > 1000) {
                log.warn("⚠️ 效能警告: {}.{} 執行時間 {}ms", 
                    metric.getClassName(),    // 類別名稱
                    metric.getMethodName(),   // 方法名稱
                    executionTime);           // 執行時間
            }
        }
    }
}