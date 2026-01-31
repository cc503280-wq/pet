package com.pet.dao.member;

import com.pet.model.member.MemberActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MemberActionLogRepository extends JpaRepository<MemberActionLog, Integer>,
        JpaSpecificationExecutor<MemberActionLog> {
		//動態查詢用

    // 根據會員ID查詢紀錄
    List<MemberActionLog> findByMemberIdOrderByActionTimeDesc(Integer memberId);

    // 根據動作類型查詢 (例如只想看 SEARCH)
    List<MemberActionLog> findByActionTypeOrderByActionTimeDesc(String actionType);

    // 根據時間範圍查詢 (用於報表匯出)
    List<MemberActionLog> findByActionTimeBetweenOrderByActionTimeDesc(LocalDateTime start,
            LocalDateTime end);
}
