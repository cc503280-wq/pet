package com.pet.service.member;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.pet.model.member.Member; 
import com.pet.dao.member.MemberRepository; 

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // Google 的唯一識別碼叫做 "sub"
        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        // 實作註冊/登入
        updateOrSaveUser(googleId, email, name, picture);

        return oAuth2User;
    }

    private void updateOrSaveUser(String googleId, String email, String name, String picture) {
        // 優先用 email 找人，或者也可以用 googleId 找
        Optional<Member> memberOptional = memberRepository.findByEmail(email);

        if (memberOptional.isPresent()) {
            // 已存在：更新資訊
            Member existingMember = memberOptional.get();
            existingMember.setGoogleId(googleId); // 補上 Google ID
            existingMember.setName(name);
            existingMember.setPicture(picture);
            memberRepository.save(existingMember);
        } else {
            // 不存在：註冊新會員
            Member newMember = Member.builder()
                    .email(email)
                    .googleId(googleId)
                    .name(name)
                    .picture(picture)
                    .password(null) // 第三方登入不設密碼
                    .build();
            // 注意：status 和 points 會由 @PrePersist 自動處理，不需 builder 賦值
            memberRepository.save(newMember);
        }
    }
}
