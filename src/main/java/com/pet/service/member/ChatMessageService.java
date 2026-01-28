package com.pet.service.member;

import com.pet.dao.member.ChatMessageRepository;
import com.pet.dao.member.MemberRepository;
import com.pet.dto.member.ChatMessageDTO;
import com.pet.model.member.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private MemberRepository memberRepository;

    // --- 新增：用來紀錄哪些會員正在「真人服務模式」 (True = 真人模式, False/Null = AI 模式) ---
    private final ConcurrentHashMap<Integer, Boolean> humanModeMap = new ConcurrentHashMap<>();

    // 檢查是否為真人模式
    public boolean isHumanMode(Integer memberId) {
        Boolean value = humanModeMap.get(memberId);
        if (value == null) {
            return false; // 找不到人，預設為 AI
        } else {
            return value; // 找到了，回傳原本的值
        }
    }

    // 切換模式
    public void setHumanMode(Integer memberId, boolean isHuman) {
        humanModeMap.put(memberId, isHuman);
    }

    /**
     * 儲存訊息
     * 
     * @param memberId 會員ID
     * @param sender   發送者 (USER, AI, ADMIN)
     * @param content  內容
     * @return 存好的訊息物件
     */
    public ChatMessage saveMessage(Integer memberId, String sender, String content) {
        ChatMessage message = ChatMessage.builder()
                .memberId(memberId)
                .sender(sender)
                .content(content)
                .createdAt(LocalDateTime.now())
                .isRead(false) // 預設未讀
                .build();

        return chatMessageRepository.save(message);
    }

    /**
     * 取得某會員的歷史對話
     */
    public List<ChatMessage> getChatHistory(Integer memberId) {
        return chatMessageRepository.findByMemberIdOrderByCreatedAtAsc(memberId);
    }

    /**
     * (後台用) 取得最近有對話的會員列表
     * 這個比較複雜，因為 Repository 只回傳了 List<Integer> (ID列表)，
     * 我們要在這裡把它轉換成 List<Member> 詳細資料回傳給前端。
     */
    public List<ChatMessageDTO> getRecentChatMembers() {
        List<Integer> memberIds = chatMessageRepository.findDistinctMemberIdsOrderByLatestMessage();
        List<ChatMessageDTO> result = new ArrayList<>();

        for (Integer id : memberIds) {
            memberRepository.findById(id).ifPresent(member -> {
                // 這裡直接呼叫剛剛抽出來的獨立方法，程式碼更乾淨！
                long unreadCount = countUnreadForAdmin(id);
                result.add(new ChatMessageDTO(member, unreadCount));
            });
        }
        return result;
    }

    /**
     * 將該會員的所有訊息標示為已讀
     * 情境：管理員點開聊天視窗時呼叫
     */
    public void markMessagesAsRead(Integer memberId) {

        List<ChatMessage> unreadMessages = chatMessageRepository.findByMemberIdAndSenderAndIsReadFalse(memberId,
                "MEMBER");
        for (ChatMessage msg : unreadMessages) {
            msg.setIsRead(true);
        }
        chatMessageRepository.saveAll(unreadMessages);
    }

    /**
     * (前台用) 會員已讀了訊息 (標記 AI 或 ADMIN 的訊息為已讀)
     */
    public void markAsReadForMember(Integer memberId) {
        // 找出所有該會員的未讀訊息
        List<ChatMessage> msgs = chatMessageRepository.findByMemberIdAndIsReadFalse(memberId);

        for (ChatMessage msg : msgs) {
            // 只標記別人寄給我的 (AI or ADMIN)
            if ("AI".equals(msg.getSender()) || "ADMIN".equals(msg.getSender())) {
                msg.setIsRead(true);
            }
        }
        chatMessageRepository.saveAll(msgs);
    }

    /**
     * 給「前台會員」用：我有幾則訊息沒看 (來源: AI 或 ADMIN)
     */
    public long countUnreadForMember(Integer memberId) {
        return chatMessageRepository.countByMemberIdAndSenderInAndIsReadFalse(
                memberId, List.of("AI", "ADMIN"));
    }

    /**
     * 給「後台管理員」用：這個會員傳了幾則訊息我還沒看 (來源: MEMBER)
     */
    public long countUnreadForAdmin(Integer memberId) {
        return chatMessageRepository.countByMemberIdAndSenderInAndIsReadFalse(
                memberId, List.of("MEMBER"));
    }

    /**
     * 結束對話 (End Session)
     * 1. 清除真人模式標記 (回歸 AI)
     * 2. 刪除該會員的所有歷史紀錄
     */
    public void endSession(Integer memberId) {
        // 1. 清除狀態 (回歸 AI 模式)
        humanModeMap.remove(memberId);

        // 2. (已修改) 不刪除資料庫紀錄，保留供管理員查閱
        // chatMessageRepository.deleteByMemberId(memberId);
    }

}