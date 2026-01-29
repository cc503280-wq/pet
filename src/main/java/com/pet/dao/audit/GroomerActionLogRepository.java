package com.pet.dao.audit;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pet.model.audit.GroomerActionLog;

/**
 * 美容師操作日誌 Repository
 * 繼承 MongoRepository，自動獲得 CRUD 方法
 */
@Repository
public interface GroomerActionLogRepository extends MongoRepository<GroomerActionLog, String> {
    
    // 依美容師 ID 查詢所有操作記錄
    List<GroomerActionLog> findByGroomerId(Integer groomerId);
    
    // 依操作類型查詢
    List<GroomerActionLog> findByActionType(String actionType);
    
    // 依美容師 ID 和操作類型查詢
    List<GroomerActionLog> findByGroomerIdAndActionType(Integer groomerId, String actionType);
    
    // 依時間範圍查詢
    List<GroomerActionLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    // 依美容師 ID 查詢，按時間倒序排列
    List<GroomerActionLog> findByGroomerIdOrderByTimestampDesc(Integer groomerId);
}