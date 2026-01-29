package com.pet.model.audit;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * 效能監控記錄
 * 記錄 Service 層方法的執行時間，用於找出系統效能瓶頸
 */
@Document(collection = "performance_metrics")
@Data
public class PerformanceMetric {
    
    @Id
    private String id;
    
    private String className;                 // 類別名稱 (如: AppointmentService)
    private String methodName;                // 方法名稱 (如: saveAppointment)
    private String methodSignature;           // 完整方法簽名
    
    private LocalDateTime timestamp;          // 執行時間點
    private Long executionTimeMs;             // 執行時間 (毫秒)
    
    private boolean success;                  // 是否成功執行
    private String errorMessage;              // 錯誤訊息 (若失敗)
    
    private Map<String, Object> parameters;   // 方法參數 (選擇性記錄)
}