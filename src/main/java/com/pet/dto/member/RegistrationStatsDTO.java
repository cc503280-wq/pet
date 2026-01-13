package com.pet.dto.member;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationStatsDTO {
	
    private List<String> labels; // 圖表橫軸 (如: 2025-01, 2025-02)
    private List<Long> data;     // 圖表縱軸 (註冊人數)
}