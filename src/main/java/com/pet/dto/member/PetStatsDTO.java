package com.pet.dto.member;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetStatsDTO {
    private List<String> labels; // 圖表標籤 (如: 狗, 貓, 其他 或 幼年, 成年)
    private List<Long> data;     // 統計數值
}