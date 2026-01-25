package com.pet.config;

import com.pet.config.SecurityConfig;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.pet.service.member.CustomOAuth2UserService;
import com.pet.util.JwtAuthenticationFilter;
import com.pet.util.OAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // 注入你寫好的 CustomOAuth2UserService
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;
    
    @Bean
    public SecurityFilterChain shopFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 擴充攔截範圍，加入 /oauth2/** 與 /login/**，這套規則才管得到 Google 登入
            .securityMatcher("/shop/**", "/api/reviews/**", "/oauth2/**", "/login/**") 
            
            // 開啟CORS支持
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOriginPatterns(List.of(
                    "http://localhost:5173",
                    "https://*.trycloudflare.com"
                ));
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(List.of("*"));
                corsConfiguration.setAllowCredentials(true);
                return corsConfiguration;
            }))
            
            // 2. 關閉 CSRF
            .csrf(csrf -> csrf.disable())
            
            // 3. 設定權限規則
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/shop/members/login", 
                    "/shop/members/register", 
                    "/shop/members/check-email", 
                    "/shop/members/check-phone",
                    "/shop/members/forgot-password", 
                    "/shop/members/reset-password",
                    // --- 新增：放行 OAuth2 必要路徑 ---
                    "/oauth2/**",
                    "/login/oauth2/**"
                ).permitAll() 
                
                .requestMatchers("/shop/products/**").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                .requestMatchers("/shop/coupons/active").permitAll() 
                .anyRequest().authenticated() 
            )

            // --- 新增：OAuth2 登入配置 ---
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                // 用這個，處理產生 Token 並轉向
                .successHandler(oAuth2SuccessHandler)
            )
            
            // 4. 改為無狀態 Session
            // 💡 提醒：OAuth2 流程中需要短暫 Session 儲存 state。
            // 如果設為 STATELESS 導致登入報錯，請改為 IF_REQUIRED
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            
            // 加入 JWT 過濾器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 密碼加密工具
    }
}