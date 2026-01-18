package com.pet.common;

/**
 * 應用程式常用常數
 */
public class AppConstants {
    
    // 預約狀態
    public static final String APPOINTMENT_STATUS_CONFIRMED = "預約確認";
    public static final String APPOINTMENT_STATUS_CANCELLED = "已取消";
    public static final String APPOINTMENT_STATUS_COMPLETED = "已完成";
    public static final String APPOINTMENT_STATUS_IN_PROGRESS = "進行中";
    public static final String APPOINTMENT_STATUS_NO_SHOW = "未到店";
    
    // 時段相關
    public static final int SLOT_DURATION_MINUTES = 15;
    
    // 預設工作時間
    public static final String DEFAULT_START_TIME = "09:00";
    public static final String DEFAULT_END_TIME = "21:00";
    
    private AppConstants() {
        // 防止實例化
    }
}
