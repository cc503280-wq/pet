package com.pet.util;

import java.time.LocalTime;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TimeSlotUtils {

	// ==========================================
	// 1. 基礎轉換工具區
	// ==========================================

	// 將陣列索引 (0-96) 轉為時間字串 "HH:mm"
	public static String indexToTime(int index) {
		int hour = index / 4;
		int minute = (index % 4) * 15;
		return String.format("%02d:%02d", hour, minute);
	}

	// 將時間字串 "HH:mm" 轉為陣列索引 (0-95)
	public static int timeToStartIndex(String time) {
		String[] parts = time.split(":");
		int h = Integer.parseInt(parts[0]);
		int m = Integer.parseInt(parts[1]);
		return (h * 4) + (m / 15);
	}
	
	public static int timeToStartIndex(LocalTime time) {
        return (time.getHour() * 4) + (time.getMinute() / 15);
    }

	// ==========================================
	// 2. 單一字串處理區
	// ==========================================

	// 找出單一字串中所有符合服務項目和加購項目的時長起始時間
	public static List<String> findAvailableTimes(String timeSlots, int durationMinutes) {
		List<String> availableTimes = new ArrayList<>();

		// 1. 計算需要連續幾格 0
		int slotsNeeded = (int) Math.ceil(durationMinutes / 15.0);

		// 2. 建立目標樣板，例如需要4格就是 "0000"
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

	// ==========================================
	// 3. 複雜運算區 (合併與檢查)
	// ==========================================

	// 檢查某段特定的時間範圍是否全為 '0'
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
	 * 
	 * @param currentSlots    目前的班表字串 (96 chars)
	 * @param startIndex      起始索引
	 * @param durationMinutes 服務總時長 (分鐘)
	 * @return 更新後的班表字串
	 */
	public static String lockSlots(String currentSlots, int startIndex, int durationMinutes) {
		// 1. 計算需要幾格
		int slotsNeeded = (int) Math.ceil(durationMinutes / 15.0);

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
	 * 鎖定時段：將指定範圍內的 '1' 改為 '0'
	 * 
	 * @param currentSlots    目前的班表字串 (96 chars)
	 * @param startIndex      起始索引
	 * @param durationMinutes 服務總時長 (分鐘)
	 * @return 更新後的班表字串
	 */
	public static String unLockSlots(String currentSlots, int startIndex, int durationMinutes) {
		// 1. 計算需要幾格
		int slotsNeeded = (int) Math.ceil(durationMinutes / 15.0);

		// 2. 轉成 char 陣列準備修改
		char[] slots = currentSlots.toCharArray();

		// 3. 執行修改 (防呆：確保不要超出陣列範圍)
		for (int i = 0; i < slotsNeeded; i++) {
			int targetIndex = startIndex + i;
			if (targetIndex < slots.length) {
				slots[targetIndex] = '0'; // 鎖定：改為 '1'
			}
		}

		// 4. 轉回字串回傳
		return new String(slots);
	}
	
	
	
}
