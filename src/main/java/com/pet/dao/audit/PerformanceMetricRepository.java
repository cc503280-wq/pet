package com.pet.dao.audit;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pet.model.audit.PerformanceMetric;

/**
 * 效能監控記錄 Repository
 */
@Repository
public interface PerformanceMetricRepository extends MongoRepository<PerformanceMetric, String> {
    
    // 依類別名稱查詢
    List<PerformanceMetric> findByClassName(String className);
    
    // 依方法名稱查詢
    List<PerformanceMetric> findByMethodName(String methodName);
    
    // 依時間範圍查詢
    List<PerformanceMetric> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    // 查詢執行時間超過指定毫秒的記錄 (找效能瓶頸)
    List<PerformanceMetric> findByExecutionTimeMsGreaterThan(Long ms);
    
    // 查詢失敗的執行記錄
    List<PerformanceMetric> findBySuccessFalse();
    
    // 依類別和方法查詢，按執行時間倒序
    List<PerformanceMetric> findByClassNameAndMethodNameOrderByExecutionTimeMsDesc(
        String className, String methodName);
}