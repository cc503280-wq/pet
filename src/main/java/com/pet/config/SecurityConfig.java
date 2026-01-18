package com.pet.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.pet.util.JwtAuthenticationFilter;

import org.springframework.core.annotation.Order;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Bean
    @Order(1) // 🟢 明確指定順序，確保先檢查 /shop/** (Prioritize shop filter chain)
    public SecurityFilterChain shopFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 只攔截路徑開頭為 /shop 的請求
            .securityMatcher("/shop/**") 
            
            //開啟CORS支持
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOriginPatterns(List.of("*")); // 🟢 修改為允許所有來源 (含手機)
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
                .requestMatchers("/shop/serviceitems/**").permitAll() // 🟢 服務項目瀏覽不擋 (Allow service items)
                .requestMatchers("/shop/groomers/**").permitAll() // 🟢 讓前端能抓到美容師資料 (Allow groomers)
                .requestMatchers("/shop/serviceitems/**").permitAll() // 🟢 服務項目瀏覽不擋 (Allow service items)
                .requestMatchers("/shop/groomers/**").permitAll() // 🟢 讓前端能抓到美容師資料 (Allow groomers)
                .requestMatchers("/shop/members/**").permitAll() // 🟢 讓前端能抓到會員資料 (Allow members)
                .requestMatchers("/shop/memberPets/**").permitAll() // 🟢 讓前端能抓到寵物資料 (Allow pets)
                .requestMatchers("/admin/**").permitAll() // 🟢 讓後台頁面能被訪問 (Allow admin pages)
                // .anyRequest().authenticated() // 🔴 暫時註解掉 (Temporarily commented out)
                .anyRequest().permitAll() // 🟢 暫時全部放行 (Temporarily permit all)
            )
            
            // 4. 改為無狀態 Session (不使用 Cookie)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
            
            // 加入這一行：在檢查帳號密碼之前，先檢查有沒有 JWT Token
            // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 🔴 暫時關閉 JWT 驗證 (Temporarily disable JWT check)

        return http.build();
    }

    // 新增：處理其他所有請求 (Admin, Legacy, API...)
    @Bean
    @Order(2)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(List.of("*"));
                corsConfiguration.setAllowCredentials(true);
                return corsConfiguration;
            }))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 全部放行，讓 WebConfig 的 Interceptor 去處理權限
            )
            .formLogin(login -> login.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 密碼加密工具
    }
}
