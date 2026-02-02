package com.pet.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dao.audit.GroomerActionLogRepository;
import com.pet.dao.audit.PerformanceMetricRepository;
import com.pet.model.audit.GroomerActionLog;
import com.pet.model.audit.PerformanceMetric;

import lombok.RequiredArgsConstructor;

/**
 * 審計日誌查詢 Controller
 * 提供後台管理員查詢操作日誌和效能監控數據的 API
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final GroomerActionLogRepository actionLogRepository;
    private final PerformanceMetricRepository metricRepository;

    // ==================== 操作日誌查詢 ====================

    /**
     * 取得所有操作日誌（分頁建議之後再加）
     */
    @GetMapping("/actions")
    public ResponseEntity<List<GroomerActionLog>> getAllActionLogs() {
        List<GroomerActionLog> logs = actionLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }

    /**
     * 依美容師 ID 查詢操作日誌
     */
    @GetMapping("/actions/groomer/{groomerId}")
    public ResponseEntity<List<GroomerActionLog>> getActionsByGroomer(
            @PathVariable Integer groomerId) {
        List<GroomerActionLog> logs = actionLogRepository.findByGroomerIdOrderByTimestampDesc(groomerId);
        return ResponseEntity.ok(logs);
    }

    /**
     * 依操作類型查詢（LOGIN, LOGOUT, CHECK_IN, COMPLETE）
     */
    @GetMapping("/actions/type/{actionType}")
    public ResponseEntity<List<GroomerActionLog>> getActionsByType(
            @PathVariable String actionType) {
        List<GroomerActionLog> logs = actionLogRepository.findByActionType(actionType);
        return ResponseEntity.ok(logs);
    }

    /**
     * 依時間範圍查詢操作日誌
     */
    @GetMapping("/actions/range")
    public ResponseEntity<List<GroomerActionLog>> getActionsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<GroomerActionLog> logs = actionLogRepository.findByTimestampBetween(start, end);
        return ResponseEntity.ok(logs);
    }

    // ==================== 效能監控查詢 ====================

    /**
     * 取得所有效能監控記錄
     */
    @GetMapping("/performance")
    public ResponseEntity<List<PerformanceMetric>> getAllPerformanceMetrics() {
        List<PerformanceMetric> metrics = metricRepository.findAll();
        return ResponseEntity.ok(metrics);
    }

    /**
     * 查詢慢速方法（執行時間超過指定毫秒）
     */
    @GetMapping("/performance/slow")
    public ResponseEntity<List<PerformanceMetric>> getSlowMethods(
            @RequestParam(defaultValue = "500") Long thresholdMs) {
        List<PerformanceMetric> metrics = metricRepository.findByExecutionTimeMsGreaterThan(thresholdMs);
        return ResponseEntity.ok(metrics);
    }

    /**
     * 查詢失敗的方法執行記錄
     */
    @GetMapping("/performance/failures")
    public ResponseEntity<List<PerformanceMetric>> getFailedExecutions() {
        List<PerformanceMetric> metrics = metricRepository.findBySuccessFalse();
        return ResponseEntity.ok(metrics);
    }

    /**
     * 取得效能統計摘要
     */
    @GetMapping("/performance/summary")
    public ResponseEntity<Map<String, Object>> getPerformanceSummary() {
        List<PerformanceMetric> allMetrics = metricRepository.findAll();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalExecutions", allMetrics.size());
        
        // 計算成功/失敗數量
        long successCount = allMetrics.stream().filter(PerformanceMetric::isSuccess).count();
        long failureCount = allMetrics.size() - successCount;
        summary.put("successCount", successCount);
        summary.put("failureCount", failureCount);
        
        // 計算平均執行時間
        double avgTime = allMetrics.stream()
            .mapToLong(PerformanceMetric::getExecutionTimeMs)
            .average()
            .orElse(0.0);
        summary.put("averageExecutionTimeMs", Math.round(avgTime * 100.0) / 100.0);
        
        // 找出最慢的方法
        allMetrics.stream()
            .max((a, b) -> Long.compare(a.getExecutionTimeMs(), b.getExecutionTimeMs()))
            .ifPresent(slowest -> {
                summary.put("slowestMethod", slowest.getClassName() + "." + slowest.getMethodName());
                summary.put("slowestTimeMs", slowest.getExecutionTimeMs());
            });
        
        return ResponseEntity.ok(summary);
    }
}