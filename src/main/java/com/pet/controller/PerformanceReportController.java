package com.pet.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dao.audit.GroomerActionLogRepository;
import com.pet.dao.audit.PerformanceMetricRepository;
import com.pet.model.audit.GroomerActionLog;
import com.pet.model.audit.PerformanceMetric;

import lombok.RequiredArgsConstructor;

/**
 * 效能報表 Controller
 * 提供統計分析 API，用於管理後台報表頁面
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class PerformanceReportController {

    private final GroomerActionLogRepository actionLogRepository;
    private final PerformanceMetricRepository metricRepository;

    /**
     * 美容師操作統計報表
     * 統計每位美容師的登入、報到、完成次數
     */
    @GetMapping("/groomer-activity")
    public ResponseEntity<List<Map<String, Object>>> getGroomerActivityReport() {
        List<GroomerActionLog> allLogs = actionLogRepository.findAll();
        
        // 依美容師分組統計
        Map<Integer, Map<String, Object>> groomerStats = new HashMap<>();
        
        for (GroomerActionLog log : allLogs) {
            Integer groomerId = log.getGroomerId();
            if (groomerId == null) continue;
            
            groomerStats.computeIfAbsent(groomerId, k -> {
                Map<String, Object> stats = new HashMap<>();
                stats.put("groomerId", groomerId);
                stats.put("groomerName", log.getGroomerName());
                stats.put("loginCount", 0);
                stats.put("logoutCount", 0);
                stats.put("checkInCount", 0);
                stats.put("completeCount", 0);
                return stats;
            });
            
            Map<String, Object> stats = groomerStats.get(groomerId);
            String actionType = log.getActionType();
            
            switch (actionType) {
                case "LOGIN" -> stats.put("loginCount", (int) stats.get("loginCount") + 1);
                case "LOGOUT" -> stats.put("logoutCount", (int) stats.get("logoutCount") + 1);
                case "CHECK_IN" -> stats.put("checkInCount", (int) stats.get("checkInCount") + 1);
                case "COMPLETE" -> stats.put("completeCount", (int) stats.get("completeCount") + 1);
            }
        }
        
        return ResponseEntity.ok(new ArrayList<>(groomerStats.values()));
    }

    /**
     * 方法效能排行榜
     * 顯示平均執行時間最長的方法 TOP N
     */
    @GetMapping("/slow-methods")
    public ResponseEntity<List<Map<String, Object>>> getSlowMethodsReport(
            @RequestParam(defaultValue = "10") int top) {
        List<PerformanceMetric> allMetrics = metricRepository.findAll();
        
        // 依類別+方法分組，計算平均執行時間
        Map<String, List<PerformanceMetric>> groupedMetrics = allMetrics.stream()
            .collect(Collectors.groupingBy(m -> m.getClassName() + "." + m.getMethodName()));
        
        List<Map<String, Object>> result = groupedMetrics.entrySet().stream()
            .map(entry -> {
                List<PerformanceMetric> metrics = entry.getValue();
                double avgTime = metrics.stream()
                    .mapToLong(PerformanceMetric::getExecutionTimeMs)
                    .average()
                    .orElse(0.0);
                long maxTime = metrics.stream()
                    .mapToLong(PerformanceMetric::getExecutionTimeMs)
                    .max()
                    .orElse(0);
                long failCount = metrics.stream()
                    .filter(m -> !m.isSuccess())
                    .count();
                
                Map<String, Object> stat = new HashMap<>();
                stat.put("method", entry.getKey());
                stat.put("callCount", metrics.size());
                stat.put("avgTimeMs", Math.round(avgTime * 100.0) / 100.0);
                stat.put("maxTimeMs", maxTime);
                stat.put("failureCount", failCount);
                return stat;
            })
            .sorted((a, b) -> Double.compare(
                (double) b.get("avgTimeMs"), 
                (double) a.get("avgTimeMs")))
            .limit(top)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 系統健康度報表
     * 統計成功率、錯誤率、平均回應時間
     */
    @GetMapping("/system-health")
    public ResponseEntity<Map<String, Object>> getSystemHealthReport() {
        List<PerformanceMetric> allMetrics = metricRepository.findAll();
        
        Map<String, Object> health = new HashMap<>();
        
        if (allMetrics.isEmpty()) {
            health.put("message", "尚無效能數據");
            return ResponseEntity.ok(health);
        }
        
        // 總執行次數
        health.put("totalExecutions", allMetrics.size());
        
        // 成功率
        long successCount = allMetrics.stream().filter(PerformanceMetric::isSuccess).count();
        double successRate = (double) successCount / allMetrics.size() * 100;
        health.put("successRate", Math.round(successRate * 100.0) / 100.0 + "%");
        
        // 平均執行時間
        double avgTime = allMetrics.stream()
            .mapToLong(PerformanceMetric::getExecutionTimeMs)
            .average()
            .orElse(0.0);
        health.put("avgResponseTimeMs", Math.round(avgTime * 100.0) / 100.0);
        
        // 最近一小時的請求數
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentCount = allMetrics.stream()
            .filter(m -> m.getTimestamp() != null && m.getTimestamp().isAfter(oneHourAgo))
            .count();
        health.put("requestsLastHour", recentCount);
        
        // 錯誤列表（最近 5 筆）
        List<Map<String, String>> recentErrors = allMetrics.stream()
            .filter(m -> !m.isSuccess())
            .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
            .limit(5)
            .map(m -> {
                Map<String, String> error = new HashMap<>();
                error.put("method", m.getClassName() + "." + m.getMethodName());
                error.put("error", m.getErrorMessage());
                error.put("time", m.getTimestamp().toString());
                return error;
            })
            .collect(Collectors.toList());
        health.put("recentErrors", recentErrors);
        
        return ResponseEntity.ok(health);
    }
}