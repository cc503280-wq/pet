package com.pet.dto.appointment;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LeaveRequestDto: 請假請求 Data Transfer Object
 * 用途：封裝前端傳來的請假申請資料 (美容師 ID, 請假日期起訖, 原因)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDto {
    
    // 美容師 ID
    private Integer groomerId;
    
    // 請假開始日期 (包含)
    private LocalDate startDate;
    
    // 請假結束日期 (包含)
    private LocalDate endDate;
    
    // 請假原因
    private String reason;
}
