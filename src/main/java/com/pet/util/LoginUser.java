package com.pet.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 代表這個註解是用在「參數」上
@Retention(RetentionPolicy.RUNTIME) // 執行時有效
public @interface LoginUser {
}