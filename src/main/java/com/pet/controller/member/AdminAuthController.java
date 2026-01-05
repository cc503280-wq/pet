package com.pet.controller.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.member.Admin;
import com.pet.service.member.AdminService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AdminAuthController {

	@Autowired
    private AdminService adminService;

    // 登入
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Admin admin = adminService.adminLogin(email, password);
        Map<String, String> response = new HashMap<>();

        if (admin != null) {
            // 登入成功：權限存入 session
            session.setAttribute("adminId", admin.getAdminId());
            session.setAttribute("role", admin.getRole());
            session.setAttribute("adminName", admin.getName()); // 新增

            response.put("status", "success");
            response.put("role", admin.getRole());
            response.put("name", admin.getName());       // 新增
            response.put("adminId", String.valueOf(admin.getAdminId())); // 新增 (轉成字串)
        } else {
            // 登入失敗
            response.put("status", "fail");
            response.put("message", "帳號或密碼錯誤");
        }
        return response;
    }

    // 登出
    @GetMapping("/logout")
    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate(); 
        }
    }
}
