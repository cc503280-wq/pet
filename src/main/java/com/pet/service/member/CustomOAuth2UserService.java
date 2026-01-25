package com.pet.service.member;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.pet.model.member.Member;
import com.pet.service.appointment.MailService;
import com.pet.dao.member.MemberRepository; 

@Service
public class CustomOAuth2UserService extends OidcUserService {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private MailService mailService;
    
    @Autowired
    private CouponUsersRealService couponUsersRealService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 先呼叫父類別方法取得原始用戶資訊
    	OidcUser oidcUser = super.loadUser(userRequest);
        
        // 2. 取得這是哪一個平台 (例如 "google" 或 "line")
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oidcUser.getAttributes();
        
        String email = null;
        String name = null;
        String picture = null;
        String googleId = null;
        String lineId = null;

        // 3. 根據平台提取欄位
        if ("google".equals(registrationId)) {
            googleId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            picture = (String) attributes.get("picture");
        } else if ("line".equals(registrationId)) {
            lineId = (String) attributes.get("sub");
            email = (String) attributes.get("email"); // 現在可以拿到 Email 了
            name = (String) attributes.get("name");   // 現在可以拿到 Name 了
            picture = (String) attributes.get("picture");
        }


        // 4. 實作註冊/登入 (傳入新的參數)
        updateOrSaveUser(googleId, lineId, email, name, picture);

        return oidcUser;
    }

    private void updateOrSaveUser(String googleId, String lineId, String email, String name, String picture) {
        // 優先用 email 找人
    	if(email == null) return;
        Optional<Member> memberOptional = memberRepository.findByEmail(email);

        if (memberOptional.isPresent()) {
            // 已存在：更新資訊
            Member existingMember = memberOptional.get();
            
            // 根據來源更新對應的 ID (原本有的保留，沒有的補上)
            if (googleId != null) existingMember.setGoogleId(googleId);
            if (lineId != null) existingMember.setLineId(lineId);
            
            existingMember.setName(name);
            existingMember.setPicture(picture);
            memberRepository.save(existingMember);
        } else {
            // 不存在：註冊新會員
            Member newMember = Member.builder()
                    .email(email)
                    .googleId(googleId)
                    .lineId(lineId) // 存入 LINE ID
                    .name(name != null ? name : "會員") 
                    .picture(picture)
                    .password(null) 
                    .build();

            Member savedMember = memberRepository.save(newMember);

            // 派發優惠券與寄送歡迎信
            couponUsersRealService.assignWelcomeCoupon(savedMember.getMemberId());
            mailService.sendWelcomeEmail(savedMember.getEmail(), savedMember.getName());
        }
    }
}