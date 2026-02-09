package com.pet.config;

import com.pet.config.SecurityConfig;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import com.pet.service.member.CustomOAuth2UserService;
import com.pet.util.JwtAuthenticationFilter;
import com.pet.util.OAuth2SuccessHandler;

import jakarta.servlet.http.HttpServletResponse;

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
                .securityMatcher("/shop/**", "/api/reviews/**", "/oauth2/**", "/login/**", "/ws-chat/**", "/orders/**")

                // 開啟CORS支持
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of(
                            "http://localhost:5173",
                            "https://*.trycloudflare.com",
                            "http://localhost:8081",
                            "https://*.ecpay.com.tw"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))

                // 2. 關閉 CSRF
                .csrf(csrf -> csrf.disable())

                // --- 新增：開啟 Frame 支援 (SockJS 必要) ---
                // 因為 SockJS 有時會用 Iframe 來模擬連線，預設 Spring Security 會擋住 (X-Frame-Options: DENY)
                // 導致 "Refused to display ... in a frame" 錯誤
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 4. 🔥 強制關閉表單登入 (這行一定要加，防止 302) 1/23加的 購物車用
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

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
                                "/login/oauth2/**",
                                "/shop/ws-chat/**",
                                "/shop/checkout/callback",
                                "/shop/checkout/map_callback")
                        .permitAll()

                        // --- 修正：ProductController 路徑設定 ---
                        .requestMatchers("/products/admin/**").authenticated() // 後台 API 需登入
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll() // 前台查詢皆公開 (含 /store/categories)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers("/shop/coupons/active").permitAll()
                        // UPDATE: 新增appointments權限
                        .requestMatchers("/appointments/**").authenticated()
                        .anyRequest().authenticated())

                // --- 新增：OAuth2 登入配置 ---
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOAuth2UserService))
                        // 用這個，處理產生 Token 並轉向
                        .successHandler(oAuth2SuccessHandler)
                        // 加上這個來捕捉錯誤！
                        .failureHandler((request, response, exception) -> {
                            System.out.println("OAuth2 失敗原因: " + exception.getMessage());
                            exception.printStackTrace(); // 這行會讓你在 Console 看到真正的錯誤
                            response.sendRedirect("http://localhost:5173/login?error=" + exception.getMessage());
                        }))

                // 4. 改為無狀態 Session
                // 💡 提醒：OAuth2 流程中需要短暫 Session 儲存 state。
                // 如果設為 STATELESS 導致登入報錯，請改為 IF_REQUIRED
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                // 當沒登入時，直接回傳 401 狀態碼，而不是轉址到登入頁
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"請先登入\"}");
                        }))

                // 加入 JWT 過濾器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 密碼加密工具
    }

    @Bean
    public JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
        OidcIdTokenDecoderFactory idTokenDecoderFactory = new OidcIdTokenDecoderFactory();

        // 設定演算法解析邏輯
        idTokenDecoderFactory.setJwsAlgorithmResolver(clientRegistration -> {
            // 對應 application.properties 中的 registrationId "line"
            if ("line".equals(clientRegistration.getRegistrationId())) {
                return MacAlgorithm.HS256;
            }
            // 其他預設使用 RS256
            return SignatureAlgorithm.RS256;
        });

        return idTokenDecoderFactory;
    }
}