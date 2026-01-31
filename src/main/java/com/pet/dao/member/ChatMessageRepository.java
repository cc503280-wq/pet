package com.pet.dao.member;

import com.pet.model.member.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    // 1. 找某個會員的歷史紀錄 (時間舊 -> 新)
    // 這樣打開聊天室時，才能把之前的對話載回來
    List<ChatMessage> findByMemberIdOrderByCreatedAtAsc(Integer memberId);

    // 2. 找「最近有聊天」的會員列表 (給管理員收件匣列表用)
    // 找出所有 sender='MEMBER' 的訊息，依照時間新->舊排序，
    // 然後用 DISTINCT 抓出不重複的 memberId。
    @Query("SELECT c.memberId FROM ChatMessage c WHERE c.sender IN ('MEMBER', 'ADMIN') GROUP BY c.memberId ORDER BY MAX(c.createdAt) DESC")
    List<Integer> findDistinctMemberIdsOrderByLatestMessage();

    // 3. 統計有多少「未讀」訊息
    long countByMemberIdAndSenderInAndIsReadFalse(Integer memberId, List<String> senders);

    // 4. 找某人傳的+未讀的 (給 markAsRead 用)
    List<ChatMessage> findByMemberIdAndSenderInAndIsReadFalse(Integer memberId, List<String> senders);

    // 5. 刪除某會員的所有對話紀錄 (End Session 用)
    void deleteByMemberId(Integer memberId);

    // --- 新增功能 ---

    // 7. 找某會員的「可見」歷史紀錄 (給前台用，過濾掉已結束的對話)
    List<ChatMessage> findByMemberIdAndIsVisibleToUserTrueOrderByCreatedAtAsc(Integer memberId);

    // 8. 軟刪除：將該會員的所有訊息設為「不可見」 (End Session 用)
    @Modifying //表示要改資料
    @Query("UPDATE ChatMessage c SET c.isVisibleToUser = false WHERE c.memberId = :memberId")
    void hideMessagesByMemberId(Integer memberId);

    // 9. 硬刪除：刪除舊資料 (排程用)
    void deleteByCreatedAtBefore(LocalDateTime cutoffDate);
}