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
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // 1. 取得 Google/LINE 資訊
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 2. 從資料庫抓取這個人
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // 3. 產生 Token
        String token = jwtUtils.createToken(member.getMemberId()); 

        // 4. 判斷是否為新註冊用戶
        // 邏輯：如果建立時間與現在時間差小於 10 秒，視為新註冊
        boolean isNew = false;
        if (member.getCreatedAt() != null) {
            long diffInSeconds = Duration.between(
                member.getCreatedAt(), 
                LocalDateTime.now()
            ).getSeconds();
            
            if (diffInSeconds < 10) { // 剛註冊 10 秒內都算新用戶
                isNew = true;
            }
        }

        // 5. 組裝跳轉 URL
        String targetUrl = "http://localhost:5173/#/login-success?token=" + token;
        if (isNew) {
            targetUrl += "&new=true";
        }
        
        // 6. 執行重導向
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}