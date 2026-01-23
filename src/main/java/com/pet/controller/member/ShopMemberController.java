package com.pet.controller.member;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pet.dto.member.LoginRequest;
import com.pet.dto.member.MemberProfileDTO;
import com.pet.dto.member.MemberRegisterDTO;
import com.pet.model.member.Member;
import com.pet.service.member.CouponUsersRealService;
import com.pet.service.member.MemberService;
import com.pet.util.JwtUtils;
import com.pet.util.LoginUser;

@RestController
@RequestMapping("/shop/members")
public class ShopMemberController {

	@Autowired
    private MemberService memberService; // 你處理資料庫邏輯的 Service
	
	@Autowired
	private CouponUsersRealService couponUsersRealService;

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
    
    //測試用
    @GetMapping("/me")
    public ResponseEntity<?> testLogin(@LoginUser Integer userId) {
        return ResponseEntity.ok("驗證成功！你的會員 ID 是: " + userId);
    }
    
    // 查詢個人詳細資料 (回傳 DTO)
    @GetMapping("/profile")
    public ResponseEntity<?> getMemberProfile(@LoginUser Integer userId) {
        if (userId == null) {
        	return ResponseEntity.status(401).body("未登入");
        }

        Member member = memberService.getMemberById(userId);
        if (member == null) {
        	return ResponseEntity.status(404).body("找不到會員");
        }

        // 將 Entity 轉為 DTO
        MemberProfileDTO dto = MemberProfileDTO.builder()
                .id(member.getMemberId())
                .email(member.getEmail())
                .name(member.getName())
                .gender(member.getGender())
                .birthday(member.getBirthday())
                .phone(member.getPhone())
                .address(member.getAddress())
                .picture(member.getPicture())
                .points(member.getPoints())
                .build();

        return ResponseEntity.ok(dto);
    }

    // 更新個人資料 (接收 DTO)
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@LoginUser Integer userId, @RequestBody MemberProfileDTO dto) {
        if (userId == null) {
        	return ResponseEntity.status(401).body("未登入");
        }

        try {
            // 為了共用原本的 Service，我們先把 DTO 轉回一個暫時的 Member 物件
            Member input = new Member();
            input.setName(dto.getName());
            input.setGender(dto.getGender());
            input.setBirthday(dto.getBirthday());
            input.setPhone(dto.getPhone());
            input.setAddress(dto.getAddress());
            input.setEmail(dto.getEmail());

            Member updated = memberService.updateMemberWithImage(userId, input, null);
            MemberProfileDTO resultDTO = MemberProfileDTO.builder()
                    .id(updated.getMemberId())
                    .name(updated.getName())
                    .email(updated.getEmail())
                    .gender(updated.getGender())
                    .birthday(updated.getBirthday())
                    .phone(updated.getPhone())
                    .address(updated.getAddress())
                    .picture(updated.getPicture())
                    .points(updated.getPoints())
                    .build();

            return ResponseEntity.ok(resultDTO);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("更新失敗");
        }
    }

    // 額外處理頭像上傳
    @PostMapping("/update-avatar")
    public ResponseEntity<?> updateAvatar(@LoginUser Integer userId, MultipartFile file) {
        if (userId == null) {
        	return ResponseEntity.status(401).body("未登入");
        }
        if (file == null || file.isEmpty()) {
        	return ResponseEntity.badRequest().body("請選擇檔案");
        }

        try {
            // 1. 建立空物件作為輸入，告訴 Service 我們這次只想更新圖片
            Member emptyInput = new Member(); 
            Member updated = memberService.updateMemberWithImage(userId, emptyInput, file);

            // 2. 將更新後的 Entity 轉為 DTO
            MemberProfileDTO resultDTO = MemberProfileDTO.builder()
                    .id(updated.getMemberId())
                    .name(updated.getName())
                    .email(updated.getEmail())
                    .gender(updated.getGender())
                    .birthday(updated.getBirthday())
                    .phone(updated.getPhone())
                    .address(updated.getAddress())
                    .picture(updated.getPicture()) // 這裡就是新的雲端網址
                    .points(updated.getPoints())
                    .build();

            return ResponseEntity.ok(resultDTO); 
        } catch (IOException e) {
            return ResponseEntity.status(500).body("圖片上傳失敗：" + e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestPart("member") MemberRegisterDTO dto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) throws IOException {

        if (memberService.findMemberByEmail(dto.getEmail()) != null) {
            return ResponseEntity.badRequest().body("此 Email 已被註冊");
        }
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()
                && memberService.findMemberByPhone(dto.getPhone()) != null) {
            return ResponseEntity.badRequest().body("此手機號碼已被註冊");
        }

        Member savedMember = memberService.register(dto, avatar);
        return ResponseEntity.ok("註冊成功，會員編號：" + savedMember.getMemberId());
    }


    
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(memberService.findMemberByEmail(email) != null);
    }

    @GetMapping("/check-phone")
    public ResponseEntity<Boolean> checkPhone(@RequestParam String phone) {
        return ResponseEntity.ok(memberService.findMemberByPhone(phone) != null);
    }
}
