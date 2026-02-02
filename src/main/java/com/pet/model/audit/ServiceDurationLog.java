package com.pet.model.audit;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * 服務時長記錄
 * 記錄從報到到完成的實際服務時間，用於分析服務項目時長是否需要調整
 */
@Document(collection = "service_duration_logs")
@Data
public class ServiceDurationLog {
    
    @Id
    private String id;
    
    private Integer appointmentId;            // 預約 ID
    private Integer groomerId;                // 執行服務的美容師 ID
    private String groomerName;               // 美容師姓名
    
    private LocalDateTime checkInTime;        // 報到時間 (掃描 QR Code)
    private LocalDateTime completeTime;       // 完成時間
    private Long durationMinutes;             // 實際服務時長 (分鐘)
    
    private List<ServiceItemInfo> serviceItems;  // 服務項目清單
    private Integer expectedDuration;         // 預估時長 (分鐘)
    private Integer durationVariance;         // 時長差異 (實際 - 預估)
    
    private String petType;                   // 寵物類型 (狗/貓)
    private String petSize;                   // 寵物體型 (小/中/大)
    private String memberName;                // 會員姓名
    
    /**
     * 服務項目資訊 (內嵌文件)
     */
    @Data
    public static class ServiceItemInfo {
        private Integer serviceItemId;
        private String serviceName;
        private Integer estimatedMinutes;     // 該項目的預估時長
    }
}