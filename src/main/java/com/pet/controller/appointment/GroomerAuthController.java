package com.pet.controller.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.annotation.LogAction;
import com.pet.model.appointment.Groomer;
import com.pet.service.appointment.GroomerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * GroomerAuthController: 專門處理美容師的認證 (登入/登出)
 */
@RestController
@RequestMapping("/groomers")
@Slf4j
public class GroomerAuthController {

    @Autowired
    private GroomerService groomerService;

    // 美容師登入 (Authentication)
    @LogAction(actionType = "LOGIN", description = "美容師登入後台")
    @PostMapping("/login") // POST /groomers/login
    public ResponseEntity<?> login(@RequestBody java.util.Map<String, String> credentials,
                                   jakarta.servlet.http.HttpSession session) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Email 和密碼不能為空");
        }
        
        try {
            // 呼叫 Service 驗證帳密 (含 BCrypt 比對)
            Groomer groomer = groomerService.groomerLogin(email, password);
            if (groomer != null) {
                // 🛡️ 安全性改進：將美容師資訊存入 Session
                // 這樣 AOP 可以從 Session 取得，不需要從前端傳遞（防止偽造）
                session.setAttribute("currentGroomerId", groomer.getGroomerId());
                session.setAttribute("currentGroomerName", groomer.getGroomerName());
                
                return ResponseEntity.ok(groomer);
            } else {
                return ResponseEntity.status(410).body("帳號或密碼錯誤");
            }
        } catch (RuntimeException e) {
            if ("ACCOUNT_DISABLED".equals(e.getMessage())) {
                return ResponseEntity.status(403).body("帳號已被停用，無法登入");
            }
            throw e;
        }
    }
    
    // 美容師登出
    @LogAction(actionType = "LOGOUT", description = "美容師登出後台")
    @PostMapping("/logout") // POST /groomers/logout
    public ResponseEntity<?> logout(@RequestBody java.util.Map<String, Object> payload,
                                    HttpSession session,
                                    HttpServletRequest request) {
        // 🔧 修正：在 invalidate Session 之前，把美容師資訊存到 Request Attribute
        // 這樣 AOP 在 @AfterReturning 執行時仍能取得美容師資訊
        Object groomerId = session.getAttribute("currentGroomerId");
        Object groomerName = session.getAttribute("currentGroomerName");
        if (groomerId != null) {
            request.setAttribute("logoutGroomerId", groomerId);
        }
        if (groomerName != null) {
            request.setAttribute("logoutGroomerName", groomerName);
        }
        
        session.invalidate();  // 清除所有 Session 資料
        return ResponseEntity.ok().body("{\"message\": \"登出成功\"}");
    }

}
