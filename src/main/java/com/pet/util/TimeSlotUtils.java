package com.pet.util;

import java.time.LocalTime;
import java.util.ArrayList;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TimeSlotUtils: 時段處理工具類
 */
public class TimeSlotUtils {


	// 將陣列索引 (0-96) 轉為時間字串 "HH:mm"
    // index 0 -> 00:00, index 1 -> 00:15
	public static String indexToTime(int index) {
		int hour = index / 4;
		int minute = (index % 4) * 15;
		return String.format("%02d:%02d", hour, minute);
	}

	// 將時間字串 "HH:mm" 轉為陣列索引 (0-95)
    // 00:00 -> 0, 00:15 -> 1
	public static int timeToStartIndex(String time) {
		String[] parts = time.split(":");
		int h = Integer.parseInt(parts[0]);
		int m = Integer.parseInt(parts[1]);
		return (h * 4) + (m / 15);
	}

	public static int timeToStartIndex(LocalTime time) {
        return (time.getHour() * 4) + (time.getMinute() / 15);
    }
    
    // 計算需要多少個 Slot (無條件進位)
    public static int calculateSlotsNeeded(int durationMinutes) {
        return (int) Math.ceil((double) durationMinutes / 15.0);
    }

	

	// 找出單一字串中所有符合服務項目和加購項目的時長起始時間
    // 回傳值：可用時段的 "開始時間" 列表 (如 ["09:00", "09:30", ...])
	public static List<String> findAvailableTimes(String timeSlots, int durationMinutes) {
		List<String> availableTimes = new ArrayList<>();

	
		int slotsNeeded = calculateSlotsNeeded(durationMinutes);

		
		String targetPattern = "0".repeat(slotsNeeded);

		// 3. 掃描字串 (滑動視窗)
		// 只需要掃到 (96 - slotsNeeded)，再往後就塞不下了
		for (int i = 0; i <= 96 - slotsNeeded; i++) {
			// 擷取當前位置開始的片段
			String segment = timeSlots.substring(i, i + slotsNeeded);

			// 如果片段完全吻合目標 (全為 0)
			if (segment.equals(targetPattern)) {
				Map<String, Object> slotMap = new HashMap<>();

				slotMap.put("slotId", i);
				slotMap.put("start", indexToTime(i));
				slotMap.put("end", indexToTime(i + slotsNeeded));
				availableTimes.add(indexToTime(i));
			}
		}

		return availableTimes;
	}


	// 檢查某段特定的時間範圍是否全為 '0'
    // 用於：多步驟驗證時，檢查該時段是否尚未使用
	public static boolean isSegmentAvailable(String slots, int start, int length) {
		// 基本防呆：避免超出 96 格導致程式崩潰
		if (start < 0 || start + length > 96) {
			return false;
		}

		// 逐格檢查
		for (int i = start; i < start + length; i++) {
			// 只要遇到 '1' (忙碌) 就失敗
			if (slots.charAt(i) == '1') {
				return false;
			}
		}
		return true; // 全部通過
	}

	/**
	 * 鎖定時段：將指定範圍內的 '0' 改為 '1'
	 * 用途：預約成立時，更新班表字串
	 * @param currentSlots    目前的班表字串 (96 chars)
	 * @param startIndex      起始索引
	 * @param durationMinutes 服務總時長 (分鐘)
	 * @return 更新後的班表字串
	 */
	public static String lockSlots(String currentSlots, int startIndex, int durationMinutes) {
		// 1. 計算需要幾格
		int slotsNeeded = calculateSlotsNeeded(durationMinutes);

		// 2. 轉成 char 陣列準備修改
		char[] slots = currentSlots.toCharArray();

		// 3. 執行修改 (防呆：確保不要超出陣列範圍)
		for (int i = 0; i < slotsNeeded; i++) {
			int targetIndex = startIndex + i;
			if (targetIndex < slots.length) {
				slots[targetIndex] = '1'; // 鎖定：改為 '1'
			}
		}

		// 4. 轉回字串回傳
		return new String(slots);
	}
	
	/**
	 * 解鎖時段：將指定範圍內的 '1' 改為 '0'
	 * 用途：取消預約時，釋放班表時段
	 * @param currentSlots    目前的班表字串 (96 chars)
	 * @param startIndex      起始索引
	 * @param durationMinutes 服務總時長 (分鐘)
	 * @return 更新後的班表字串
	 */
	public static String unLockSlots(String currentSlots, int startIndex, int durationMinutes) {
		// 1. 計算需要幾格
		int slotsNeeded = calculateSlotsNeeded(durationMinutes);

		// 2. 轉成 char 陣列準備修改
		char[] slots = currentSlots.toCharArray();

		// 3. 執行修改 (防呆：確保不要超出陣列範圍)
		for (int i = 0; i < slotsNeeded; i++) {
			int targetIndex = startIndex + i;
			if (targetIndex < slots.length) {
				slots[targetIndex] = '0'; // 解鎖：改為 '0'
			}
		}

		// 4. 轉回字串回傳
		return new String(slots);
	}
	
}
