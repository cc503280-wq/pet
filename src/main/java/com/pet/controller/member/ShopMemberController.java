package com.pet.controller.member;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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

import com.pet.aspect.LogAction;
import com.pet.dao.member.MemberActionLogRepository;
import com.pet.dto.member.LoginRequest;
import com.pet.dto.member.MemberProfileDTO;
import com.pet.dto.member.MemberRegisterDTO;
import com.pet.model.member.Member;
import com.pet.model.member.MemberActionLog;
import com.pet.service.appointment.MailService;
import com.pet.service.member.CouponUsersRealService;
import com.pet.service.member.MemberService;
import com.pet.util.JwtUtils;
import com.pet.util.LoginUser;
import jakarta.servlet.http.HttpServletRequest; // Add Import

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

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MailService mailService;

    @Autowired
    private MemberActionLogRepository logRepository; // [AOP] 手動注入

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        // 1. 去資料庫找會員
        Member member = memberService.findMemberByEmail(loginRequest.getEmail());

        // 2. 檢查會員是否存在，以及密碼是否正確
        if (member != null && passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {

            // 3. 登入成功，產生 Token (傳入你的 Integer ID)
            String token = jwtUtils.createToken(member.getMemberId());

            // [AOP] 紀錄登入
            try {
                MemberActionLog log = MemberActionLog.builder()
                        .memberId(member.getMemberId())
                        .actionType(LogAction.ActionType.LOGIN)
                        .detail("一般登入")
                        .clientIp(request.getRemoteAddr())
                        .build();
                logRepository.save(log);
            } catch (Exception e) {
                System.err.println("一般登入紀錄失敗: " + e.getMessage());
            }

            // 4. 回傳給前端
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "登入成功");
            return ResponseEntity.ok(response);

        } else {
            return ResponseEntity.status(401).body("帳號或密碼錯誤");
        }
    }

    // 1. 忘記密碼：發送重設信件
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        // 先確認資料庫有無此會員
        Member member = memberService.findMemberByEmail(email);

        // 基於安全考量，無論帳號是否存在，都回傳同樣的訊息，避免駭客探測 Email
        if (member != null) {
            // 生成 Token
            String token = UUID.randomUUID().toString();
            String redisKey = "auth:reset_token:" + token;

            // 存入 Redis (15 分鐘過期)，Value 存 Email
            redisTemplate.opsForValue().set(redisKey, email, 15, TimeUnit.MINUTES);

            // 呼叫寫好的 MailService 寄信
            mailService.sendForgotPasswordEmail(email, token);
        }

        return ResponseEntity.ok(Map.of("message", "重設連結已發送至您的信箱，請前往確認"));
    }

    // 2. 重設密碼：驗證 Token 並更新
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        String redisKey = "auth:reset_token:" + token;

        // 1. 從 Redis 抓取 Email
        String email = redisTemplate.opsForValue().get(redisKey);

        if (email == null) {
            return ResponseEntity.status(400).body("連結已過期或無效");
        }

        // 2. 找到該會員並更新密碼
        Member member = memberService.findMemberByEmail(email);
        if (member != null) {
            // 記得一定要加密新密碼
            member.setPassword(passwordEncoder.encode(newPassword));

            // 使用你現有的 service 保存 (假設 memberService 有 update 方法)
            // 如果沒有單純更換密碼的方法，建議在 MemberService 補一個
            memberService.updateMemberPassword(member.getMemberId(), member.getPassword());

            // 3. 修改成功後刪除 Redis Token
            redisTemplate.delete(redisKey);

            return ResponseEntity.ok(Map.of("message", "密碼重設成功，請重新登入"));
        }

        return ResponseEntity.status(404).body("找不到對應的會員");
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

    // 會員註冊
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestPart("member") MemberRegisterDTO dto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) throws IOException {

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

    // 檢查email重複
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(memberService.findMemberByEmail(email) != null);
    }

    // 檢查手機重複
    @GetMapping("/check-phone")
    public ResponseEntity<Boolean> checkPhone(@RequestParam String phone) {
        return ResponseEntity.ok(memberService.findMemberByPhone(phone) != null);
    }
}
