package com.pet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.pet.util.AdminAuthInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

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
                        "/css/**", "/js/**", "/images/**",// 排除靜態資源
                        "/products/store/**", 
                        "/api/**"
                );
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允許後端所有網址
                .allowedOrigins("http://localhost:5173") // 🟢 指定允許的前端網址 (注意 Port 要對)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許的動作
                .allowCredentials(true);
    }

}
