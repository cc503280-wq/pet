package com.pet.dao.audit;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pet.model.audit.ServiceDurationLog;

/**
 * 服務時長記錄 Repository
 */
@Repository
public interface ServiceDurationLogRepository extends MongoRepository<ServiceDurationLog, String> {
    
    // 依預約 ID 查詢
    ServiceDurationLog findByAppointmentId(Integer appointmentId);
    
    // 依美容師 ID 查詢
    List<ServiceDurationLog> findByGroomerId(Integer groomerId);
    
    // 依時間範圍查詢
    List<ServiceDurationLog> findByCompleteTimeBetween(LocalDateTime start, LocalDateTime end);
    
    // 依寵物類型查詢
    List<ServiceDurationLog> findByPetType(String petType);
    
    // 依寵物體型查詢
    List<ServiceDurationLog> findByPetSize(String petSize);
    
    // 查詢超時的服務 (實際時長 > 預估時長)
    List<ServiceDurationLog> findByDurationVarianceGreaterThan(Integer variance);
}