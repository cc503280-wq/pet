package com.pet.model.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "chat_message")
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 發送者 ID (若是 AI 或 ADMIN，可為 null 或特定 ID，這邊暫定 0 或 null)
    @Column(name = "member_id")
    private Integer memberId; // 這是存資料庫用的純 ID (FK)
    
    @ManyToOne 
    @JoinColumn(name = "member_id", insertable = false, updatable = false) // 這裡指明關聯到 member_id
    private Member member; // 這是抓資料用的物件
    
    // 發送者類型: "MEMBER", "AI", "ADMIN"
    private String sender; 

    // 內容 (使用 NVARCHAR(MAX) 支援中文長字串)
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    // 建立時間
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // 訊息狀態 (是否已讀，給管理員後台用)
    private Boolean isRead = false;
}
