package com.pet.config;

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

import com.pet.util.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Bean
    public SecurityFilterChain shopFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 只攔截路徑開頭為 /shop 的請求
            .securityMatcher("/shop/**", "/api/reviews/**") 
            
            //開啟CORS支持
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new CorsConfiguration();

                //FIXME: 增加cloudflare的host
                corsConfiguration.setAllowedOriginPatterns(List.of(
                    "http://localhost:5173",
                    "https://*.trycloudflare.com"
                ));
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(List.of("*"));
                corsConfiguration.setAllowCredentials(true);
                return corsConfiguration;
            }))
            
            // 2. 關閉 CSRF 跨站請求偽造 (因為前後端分離使用 JWT，不需要這個)
            .csrf(csrf -> csrf.disable())
            
            // 3. 設定權限規則
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/shop/members/login", "/shop/members/register").permitAll() // 登入註冊不擋
                .requestMatchers("/shop/products/**").permitAll() // 商品瀏覽不擋
                .requestMatchers(HttpMethod.GET,"/api/reviews/**").permitAll()
                //FIXME: 允許未登入查看優惠券
                .requestMatchers("/shop/coupons/active").permitAll() // 允許未登入查看優惠券
                .requestMatchers("/appointments/**").authenticated()//需要登入
                .anyRequest().authenticated() // 其他 /shop 下的所有請求都要登入
            )
            
            // 4. 改為無狀態 Session (不使用 Cookie)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 加入這一行：在檢查帳號密碼之前，先檢查有沒有 JWT Token
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 密碼加密工具
    }
}
