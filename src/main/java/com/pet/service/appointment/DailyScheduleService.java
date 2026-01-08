package com.pet.service.appointment;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pet.dao.appointment.DailyScheduleRepository;
import com.pet.util.TimeSlotUtils;

@Service
public class DailyScheduleService {
	@Autowired	
	private DailyScheduleRepository dailyScheduleRepository;

	
	public List<Map<String, Object>> getAvailableTimeSlots(LocalDate date, int totalDuration) {
		
		System.out.println("====== [後端 Debug 開始] ======");
        System.out.println("查詢參數: 日期=" + date + ", 時長=" + totalDuration + ", 模式=");

        List<Map<String, Object>> schedules = dailyScheduleRepository.findSchedulesByDate(date);
        
        System.out.println("資料庫撈到的班表數量: " + schedules.size());
        
        if (schedules.size() > 0) {
            System.out.println("第一筆資料內容: " + schedules.get(0));
        }
        
     // 2. 遍歷資料並計算空檔
        return schedules.stream()
            .map(row -> {
                // 從資料庫取值 (對應 SQL 裡的 AS 別名)
                // ⚠️ 注意：有些資料庫回傳 ID 會是 BigInteger，若報錯可改成 row.get("groomerId").toString()
                Object id = row.get("groomerId"); 
                String name = (String) row.get("name");
                String timeSlots =  (String) row.get("timeSlots");

                // 3. 呼叫工具類別計算「開始時間」
                List<String> availableTimes = TimeSlotUtils.findAvailableTimes(timeSlots, totalDuration);
                System.out.println("  -> 美容師 " + id + " 找到的空檔數量: " + availableTimes.size());
                // 4. 包裝成前端要的結構
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("groomerId", id);
                resultMap.put("groomName", name);
                resultMap.put("slots", availableTimes);
                
                return resultMap;
            })
            // 5. 過濾掉沒有空檔的美容師
            .filter(map -> {
                List<?> times = (List<?>) map.get("slots");
                return times != null && !times.isEmpty();
            })
            .collect(Collectors.toList());
    }
	
	
}