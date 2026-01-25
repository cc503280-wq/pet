package com.pet.util;

import com.pet.model.member.Member;
import com.pet.dao.member.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // 1. 取得 Google 資訊
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); //目前是誰登入
        String email = oAuth2User.getAttribute("email");

        // 2. 從資料庫抓取這個人（因為 CustomOAuth2UserService 已經先幫你存好資料了，這裡一定找得到）
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // 3. 產生 Token (傳入 memberId，對應你的 jwtUtils 邏輯)
        // 假設你的 jwtUtils.createToken 接收的是 Integer ID
        String token = jwtUtils.createToken(member.getMemberId()); 

        // 4. 跳轉回前端 Vue，把 Token 帶在網址
        // 注意：這裡使用 #/ 或是 ? 視你的 Vue Router 模式而定
        String targetUrl = "http://localhost:5173/#/login-success?token=" + token;
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}