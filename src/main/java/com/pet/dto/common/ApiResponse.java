package com.pet.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 統一 API 回應格式
 * 用於標準化所有 REST API 的 JSON 回應結構
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    
    private boolean success;
    private String message;
    private Object data;
    
    // 成功回應 (帶資料)
    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }
    
    // 成功回應 (不帶資料)
    public static ApiResponse success(String message) {
        return new ApiResponse(true, message, null);
    }
    
    // 失敗回應
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }
}
