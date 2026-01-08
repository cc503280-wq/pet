package com.pet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.pet.util.AdminAuthInterceptor;

public class WebConfig implements WebMvcConfigurer {
    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // // 當網址請求 /memberImages/** 時，對應到實體路徑 C:/memberImages/
    // registry.addResourceHandler("/memberImages/**")
    // .addResourceLocations("file:///C:/memberImages/");
    // }

    @Autowired
    private AdminAuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // 攔截所有路徑
                .excludePathPatterns(
                        "/auth/login", // 排除登入 API
                        "/auth/logout", // 排除登出 API
                        "/admin/layout/Login.html", // 排除登入頁面
                        "/css/**", "/js/**", "/images/**" // 排除靜態資源
                );
    }

}
