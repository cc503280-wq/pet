package com.pet.model.audit;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * 美容師操作日誌
 * 記錄：登入、登出、掃描 QR Code、報到、完成服務等操作
 */
@Document(collection = "groomer_action_logs")  // 對應 MongoDB 的 collection 名稱
@Data
public class GroomerActionLog {
    
    @Id
    private String id;                        // MongoDB 自動生成的唯一 ID
    
    private Integer groomerId;                // 美容師 ID
    private String groomerName;               // 美容師姓名
    private String actionType;                // 操作類型: LOGIN, LOGOUT, CHECK_IN, COMPLETE
    
    private Integer targetAppointmentId;      // 相關預約 ID
    private Integer targetMemberId;           // 相關會員 ID 
    private String targetMemberName;          // 會員姓名
    
    private LocalDateTime timestamp;          // 操作時間
    private String ipAddress;                 // IP 地址
    private String userAgent;                 // 瀏覽器資訊
    
    private Map<String, Object> details;      // 額外詳情 (彈性欄位)
}