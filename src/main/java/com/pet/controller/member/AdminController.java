package com.pet.controller.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.member.Admin;
import com.pet.service.member.AdminService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/admins")
public class AdminController {

	@Autowired
    private AdminService adminService;

    // 權限檢查輔助方法
    private boolean isSuperAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "super_admin".equals(role);
    }

    // 查詢列表
    @GetMapping
    public List<Admin> listAdmins(@RequestParam(required = false, defaultValue = "all") String status) {
        List<Admin> admins;
        if ("all".equals(status)) {
            admins = adminService.getAllAdmins();
        } else {
            admins = adminService.getAdminsByStatus(status);
        }
        return admins;
    }

    // 依 ID 查詢
    @GetMapping("/{id}")
    public Admin getById(@PathVariable int id) {
        Admin admin = adminService.getAdminById(id);
        return admin; // 找不到就是 null
    }

    // 模糊查詢姓名 
    @GetMapping("/search")
    public List<Admin> searchByName(@RequestParam String name) {
        return adminService.searchAdminsByName(name);
    }

    // 新增管理員 
    @PostMapping
    public Admin create(@RequestBody Admin input, HttpSession session) {
    	if (!isSuperAdmin(session)) {
            throw new RuntimeException("權限不足"); // 或是拋出自定義異常
        }
        return adminService.createAdmin(input);
    }

    // 修改資料 
    @PutMapping("/{id}")
    public Admin update(@PathVariable int id, @RequestBody Admin input, HttpSession session) {
    	if (!isSuperAdmin(session)) {
            throw new RuntimeException("權限不足"); // 或是拋出自定義異常
        }
        return adminService.updateAdmin(input);
    }

    // 切換狀態 
    @PatchMapping("/{id}/toggle")
    public boolean toggleStatus(@PathVariable int id) {
        boolean success = adminService.toggleAdminStatus(id);
        return success;
    }
}
