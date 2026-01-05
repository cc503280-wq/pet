package com.pet.service.member;

import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dao.member.AdminRepository;
import com.pet.model.member.Admin;


@Service
@Transactional
public class AdminService {

	@Autowired
    private AdminRepository adminRepo;

    // 查詢全部
    public List<Admin> getAllAdmins() {
        return adminRepo.findAllByOrderByAdminIdAsc();
    }

    // 依狀態查詢
    public List<Admin> getAdminsByStatus(String status) {
        return adminRepo.findByStatusOrderByAdminId(status);
    }

    // 依 ID 查詢
    public Admin getAdminById(int id) {
        Optional<Admin> optional = adminRepo.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    // 模糊查詢姓名
    public List<Admin> searchAdminsByName(String name) {
        return adminRepo.findByNameContaining(name);
    }

    // 新增 (使用 Builder)
    public Admin createAdmin(Admin input) {
        // 加密
        String hashedPassword = BCrypt.hashpw(input.getPassword(), BCrypt.gensalt());

        // 使用 Builder 模式清楚地建立新物件
        Admin newAdmin = Admin.builder()
                .name(input.getName())
                .email(input.getEmail())
                .password(hashedPassword)
                .phone(input.getPhone())
                .role(input.getRole() != null ? input.getRole() : "admin")
                .status("active")
                .build();

        return adminRepo.save(newAdmin);
    }

    // 6. 修改
    public Admin updateAdmin(Admin input) {
        Optional<Admin> optional = adminRepo.findById(input.getAdminId());
        
        if (optional.isPresent()) {
            Admin existing = optional.get();
            
            // 手動更新欄位
            existing.setName(input.getName());
            existing.setEmail(input.getEmail());
            existing.setPhone(input.getPhone());
            existing.setRole(input.getRole());
            
            if (input.getStatus() != null) {
                existing.setStatus(input.getStatus());
            }
            
            return adminRepo.save(existing);
        }
        
        return null; // 找不到該筆資料
    }

    // 7. 切換狀態
    public boolean toggleAdminStatus(int id) {
        Optional<Admin> optional = adminRepo.findById(id);
        
        if (optional.isPresent()) {
            Admin admin = optional.get();
            String currentStatus = admin.getStatus();
            
            // 傳統 if-else 切換
            if ("active".equals(currentStatus)) {
                admin.setStatus("disabled");
            } else {
                admin.setStatus("active");
            }
            
            adminRepo.save(admin);
            return true;
        }
        
        return false;
    }
    
 // 登入驗證
    public Admin adminLogin(String email, String password) {
        Optional<Admin> optional = adminRepo.findByEmail(email);

        if (optional.isPresent()) {
            Admin admin = optional.get();
            
            if (BCrypt.checkpw(password, admin.getPassword())) {
                if ("active".equals(admin.getStatus())) {
                    return admin;
                }
            }
        }
        
        return null;
    }
}
