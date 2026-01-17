package com.pet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
    public SecurityFilterChain shopFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 只攔截路徑開頭為 /shop 的請求
            .securityMatcher("/shop/**") 
            
            // 2. 關閉 CSRF 跨站請求偽造 (因為前後端分離使用 JWT，不需要這個)
            .csrf(csrf -> csrf.disable())
            
            // 3. 設定權限規則
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/shop/member/login", "/shop/member/register").permitAll() // 登入註冊不擋
                .requestMatchers("/shop/products/**").permitAll() // 商品瀏覽不擋
                .anyRequest().authenticated() // 其他 /shop 下的所有請求都要登入
            )
            
            // 4. 改為無狀態 Session (不使用 Cookie)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 密碼加密工具
    }
}
