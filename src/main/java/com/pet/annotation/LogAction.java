package com.pet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自訂註解：標記需要記錄操作日誌的方法
 * 
 * 使用方式：
 * @LogAction(actionType = "LOGIN", description = "美容師登入")
 * public void login() { ... }
 */
@Target(ElementType.METHOD)      // 只能用在方法上
@Retention(RetentionPolicy.RUNTIME)  // 執行時期保留（AOP 需要）
public @interface LogAction {
    
    /**
     * 操作類型
     * 例如: LOGIN, LOGOUT, CHECK_IN, COMPLETE
     */
    String actionType();
    
    /**
     * 操作描述（選填）
     */
    String description() default "";
}