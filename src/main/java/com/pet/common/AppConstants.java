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
    public static final String APPOINTMENT_STATUS_CHECKED_IN = "已報到";
    public static final String APPOINTMENT_STATUS_NO_SHOW = "未到店";
    
    // 時段相關
    public static final int SLOT_DURATION_MINUTES = 15;
    
    // 預設工作時間
    public static final String DEFAULT_START_TIME = "09:00";
    public static final String DEFAULT_END_TIME = "21:00";
    
    // WebSocket Topic
    public static final String TOPIC_ADMIN_APPOINTMENTS = "/topic/admin/appointments";
    public static final String TOPIC_MEMBER_PREFIX = "/topic/member/%d/appointments";
    public static final String TOPIC_GROOMER_PREFIX = "/topic/groomer/%d/appointments";

    // WebSocket Event Type
    public static final String EVENT_NEW_APPOINTMENT = "NEW_APPOINTMENT";
    public static final String EVENT_CANCELLED = "APPOINTMENT_CANCELLED";
    public static final String EVENT_COMPLETED = "APPOINTMENT_COMPLETED";
    public static final String EVENT_CHECKIN = "APPOINTMENT_CHECKIN";
    public static final String EVENT_START = "APPOINTMENT_START";

    private AppConstants() {
        // 防止實例化
    }
}
