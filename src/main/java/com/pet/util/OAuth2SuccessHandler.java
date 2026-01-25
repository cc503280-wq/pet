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
        
        // 1. 取得 OAuth2 用戶資訊
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        // 💡 修正：增加防呆，避免拿不到 Email 導致後續報錯
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            System.err.println("OAuth2 登入失敗：無法從提供者獲取 Email");
            response.sendRedirect("http://localhost:5173/#/login?error=no_email");
            return;
        }

        // 2. 從資料庫抓取這個人 (這時 CustomOAuth2UserService 應該已經幫我們存好或更新好了)
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("找不到使用者: " + email));

        // 3. 產生 JWT Token
        String token = jwtUtils.createToken(member.getMemberId()); 

        // 4. 判斷是否為新註冊用戶 (10 秒邏輯)
        boolean isNew = false;
        if (member.getCreatedAt() != null) {
            long diffInSeconds = Duration.between(
                member.getCreatedAt(), 
                LocalDateTime.now()
            ).getSeconds();
            
            if (diffInSeconds < 10) {
                isNew = true;
            }
        }

        // 5. 💡 結合動態網域判定 (本機 5173 vs Cloudflare Tunnel)
        String requestHost = request.getServerName(); // 取得當前請求的域名
        String frontendBaseUrl;

        // 如果域名包含 trycloudflare，表示使用者是從外網連進來的
        if (requestHost.contains("trycloudflare.com")) {
            // 自動組裝 https://你的域名 (通常前端也在同一個 Tunnel 下)
            frontendBaseUrl = "https://" + requestHost;
        } else {
            // 否則預設為本機開發環境
            frontendBaseUrl = "http://localhost:5173";
        }

        // 6. 組裝跳轉 URL 並執行重導向
        // 使用 Vue Hash 模式，將參數放在 #/ 之後
        String targetUrl = frontendBaseUrl + "/#/login-success?token=" + token;
        if (isNew) {
            targetUrl += "&new=true";
        }
        
        System.out.println("OAuth2 登入成功，導向至: " + targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}