package com.pet.dao.member;

import com.pet.model.member.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
    List<ChatMessage> findByMemberIdAndSenderAndIsReadFalse(Integer memberId, String sender);

    // 5. 刪除某會員的所有對話紀錄 (End Session 用)
    void deleteByMemberId(Integer memberId);

    // 6. 找某會員所有的未讀訊息 (不分 sender，給 markAsReadForMember 用)
    List<ChatMessage> findByMemberIdAndIsReadFalse(Integer memberId);
}