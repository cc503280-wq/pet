package com.pet.util;

import com.pet.model.member.Member;
import com.pet.model.member.MemberActionLog;
import com.pet.service.appointment.MailService;
import com.pet.service.member.CouponUsersRealService;
import com.pet.aspect.LogAction;
import com.pet.dao.member.MemberActionLogRepository;
import com.pet.dao.member.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MemberActionLogRepository logRepository; // [AOP] 手動注入

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private CouponUsersRealService couponUsersRealService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String provider = authToken.getAuthorizedClientRegistrationId(); // google 或 line
        OAuth2User oAuth2User = authToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 1. 解析資料
        String email = null;
        String name = null;
        String picture = null;
        String googleId = null;
        String lineId = null;

        if ("google".equals(provider)) {
            googleId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            picture = (String) attributes.get("picture");
        } else if ("line".equals(provider)) {
            lineId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            picture = (String) attributes.get("picture");

            // LINE 的大頭貼欄位有時候叫 pictureUrl
            if (picture == null) {
                picture = (String) attributes.get("pictureUrl");
            }
        }

        // 2. 防呆：如果真的拿不到 Email
        if (email == null) {
            System.err.println("OAuth2 登入失敗：無法獲取 Email");
            response.sendRedirect("http://localhost:5173/#/login?error=no_email");
            return;
        }

        // 3. 整合註冊/登入邏輯 (避免 Race Condition，移轉自 CustomOAuth2UserService)
        String finalName = (name != null) ? name : "新會員";
        String finalPicture = picture;
        String finalGoogleId = googleId;
        String finalLineId = lineId;

        // 優先查找會員
        Member member = memberRepository.findByEmail(email).orElse(null);
        boolean isNew = false; // 用於判斷前端顯示

        if (member != null) {
            // 已存在：檢查是否需要更新 ID (綁定帳號)
            boolean updated = false;
            if (finalGoogleId != null && member.getGoogleId() == null) {
                member.setGoogleId(finalGoogleId);
                updated = true;
            }
            if (finalLineId != null && member.getLineId() == null) {
                member.setLineId(finalLineId);
                updated = true;
            }
            // 更新基本資料 (可選，確保頭像是最新的)
            if (finalPicture != null && !finalPicture.equals(member.getPicture())) {
                member.setPicture(finalPicture);
                updated = true;
            }

            // 使用 saveAndFlush 確保立即寫入
            member = memberRepository.saveAndFlush(member);
            System.out.println("✅ OAuth2 舊會員登入成功 (ID:" + member.getMemberId() + "): " + email);
        } else {
            // 不存在：註冊新會員
            System.out.println("🎉 OAuth2 發現新用戶，執行註冊: " + email);
            isNew = true;

            Member newMember = new Member();
            newMember.setEmail(email);
            newMember.setName(finalName);
            newMember.setPicture(finalPicture);
            newMember.setGoogleId(finalGoogleId);
            newMember.setLineId(finalLineId);
            newMember.setCreatedAt(LocalDateTime.now());

            // 使用 saveAndFlush 確保立即寫入
            member = memberRepository.saveAndFlush(newMember);

            // 派發優惠券與寄送歡迎信
            try {
                couponUsersRealService.assignWelcomeCoupon(member.getMemberId());
                mailService.sendWelcomeEmail(member.getEmail(), member.getName());
            } catch (Exception e) {
                System.err.println("⚠️ 歡迎信/優惠券派發失敗: " + e.getMessage());
            }
        }

        // [AOP] 手動紀錄登入行為 (OAuth2)
        try {
            MemberActionLog log = MemberActionLog.builder()
                    .memberId(member.getMemberId())
                    .actionType(LogAction.ActionType.LOGIN)
                    .detail("OAuth2登入 (" + (googleId != null ? "Google" : "LINE") + ")")
                    .clientIp(request.getRemoteAddr())
                    .build();
            logRepository.save(log);
        } catch (Exception e) {
            System.err.println("OAuth2 登入紀錄失敗: " + e.getMessage());
        }

        // 4. 產生 Token
        String token = jwtUtils.createToken(member.getMemberId());

        // 5. 動態網域判定
        String requestHost = request.getServerName();
        String frontendBaseUrl;
        if (requestHost.contains("trycloudflare.com")) {
            frontendBaseUrl = "https://" + requestHost;
        } else {
            frontendBaseUrl = "http://localhost:5173";
        }

        // 6. 導向
        String targetUrl = frontendBaseUrl + "/#/login-success?token=" + token;
        if (isNew) {
            targetUrl += "&new=true";
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}