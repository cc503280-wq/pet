package com.pet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.pet.util.AdminAuthInterceptor;
import com.pet.util.LoginUserHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminAuthInterceptor authInterceptor;

    @Autowired
    private LoginUserHandlerMethodArgumentResolver loginUserResolver;

    // 註冊 @LoginUser
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // 攔截所有路徑
                .excludePathPatterns(
                        "/shop/**",
                        "/auth/login", // 排除登入 API
                        "/auth/logout", // 排除登出 API
                        "/admin/layout/Login.html", // 排除登入頁面
                        "/css/**", "/js/**", "/images/**", // 排除靜態資源
                        "/products/store/**",
                        "/api/**",
                        "/favicon.ico",
                        "/appointments/member/**", // 會員查詢預約
                        "/appointments/insertInto", // 會員新增預約
                        "/appointments/cancel/**", // 會員取消預約
                        "/appointments/*/qrcode", // QR Code 圖片 API (前台使用)
                        "/appointments/groomer/**", // 美容師查詢任務
                        "/appointments/complete/**", // 美容師完成任務
                        "/appointments/check-in/**", // 預約報到
                        "/serviceitems/**", // 服務項目 API (前台瀏覽)
                        "/groomers/**", // 美容師 API (前台瀏覽)
                        "/schedule/**", // 時段 API (前台預約)
                        "/members", // 會員列表 API (預約選擇會員)
                        "/memberPets/**"); // 會員寵物 API (預約選擇寵物)
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允許後端所有網址
                .allowedOriginPatterns("*") // 🟢 改為允許所有來源 (支援 ngrok)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 允許的動作
                .allowCredentials(true);
    }

}
