package com.pet.controller.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dto.member.LoginRequest;
import com.pet.model.member.Member;
import com.pet.service.member.MemberService;
import com.pet.util.JwtUtils;

@RestController
@RequestMapping("/shop/members")
public class ShopMemberController {

	@Autowired
    private MemberService memberService; // 你處理資料庫邏輯的 Service

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 1. 去資料庫找會員
        Member member = memberService.findMemberByEmail(loginRequest.getEmail());
        
        // 2. 檢查會員是否存在，以及密碼是否正確
        if (member != null && passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            
            // 3. 登入成功，產生 Token (傳入你的 Integer ID)
            String token = jwtUtils.createToken(member.getMemberId());

            // 4. 回傳給前端
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "登入成功");
            return ResponseEntity.ok(response);
            
        } else {
            return ResponseEntity.status(401).body("帳號或密碼錯誤");
        }
    }
}
