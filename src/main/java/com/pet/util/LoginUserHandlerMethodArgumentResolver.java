package com.pet.util;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    // 1. 判斷現在這個參數是否帶有 @LoginUser 註解
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class) &&
               parameter.getParameterType().equals(Integer.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        
        // 1. 先取出 Authentication 物件
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 🔥 關鍵判斷：如果是訪客，或者是 Spring Security 預設的匿名使用者
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            return null; // 這樣 Controller 收到的 memberId 就會是 null，不會報錯
        }

        // 3. 取出 Principal
        Object principal = authentication.getPrincipal();

        // 4. 根據您 JWT Filter 的設定取出 ID
        if (principal instanceof Integer userId) {
            return userId; 
        }
        
        // 如果您的 Principal 是 UserDetails 物件，則需要轉型後拿 ID
        // if (principal instanceof MyUserDetails user) { return user.getId(); }

        return null;
    }
}