package com.pet.dto.common;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 統一 API 回應格式 (泛型版本)
 * 用於標準化所有 REST API 的 JSON 回應結構
 * 
 * @param <T> data 欄位的型別
 */
@Data
@NoArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String errorCode;  // 錯誤代碼，方便前端判斷錯誤類型
    private LocalDateTime timestamp = LocalDateTime.now();  // 回應時間戳記
    
    // 私有建構子，用於靜態工廠方法
    private ApiResponse(boolean success, String message, T data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
    
    // ==================== 成功回應 ====================
    
    /**
     * 成功回應 (帶資料)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }
    
    /**
     * 成功回應 (不帶資料)
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }
    
    // ==================== 失敗回應 ====================
    
    /**
     * 失敗回應 (帶錯誤代碼) - 推薦使用
     * @param errorCode 錯誤代碼，如 "SLOT_TAKEN", "MEMBER_NOT_FOUND"
     * @param message 錯誤訊息
     */
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(false, message, null, errorCode);
    }
    
    /**
     * 失敗回應 (僅訊息) - 向後相容舊程式碼
     * @param message 錯誤訊息
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }
}
