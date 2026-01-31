package com.pet.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定義 LogAction 註解
 * 加在 Service 方法上，用於自動紀錄會員行為
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAction {

    // 行為類型 (必填)
    String type();

    // 對應的 Action Type 常數
    class ActionType {
        public static final String LOGIN = "LOGIN"; // 登入
        public static final String SEARCH = "SEARCH"; // 搜尋
        public static final String CART = "CART"; // 購物車 (加入/移除/更新)
        public static final String CREATE_ORDER = "CREATE_ORDER"; // 建立訂單
        public static final String BOOKING = "BOOKING"; // 預約美容
        public static final String FAVORITE = "FAVORITE"; // 收藏商品
    }
}
