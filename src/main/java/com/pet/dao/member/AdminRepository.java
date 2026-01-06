package com.pet.dao.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pet.model.member.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

	// 登入：由 email 找完整 Admin
    Optional<Admin> findByEmail(String email);

    // 模糊查詢姓名
    List<Admin> findByNameContaining(String name);

    // 根據狀態查詢
    List<Admin> findByStatusOrderByAdminId(String status);
    
    // 查詢全部並排序
    List<Admin> findAllByOrderByAdminIdAsc();
}
