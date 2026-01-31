package com.pet.model.member;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_action_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "action_type", nullable = false)
    private String actionType; // SEARCH, LOGIN, ADD_TO_CART, CREATE_ORDER, BOOKING

    @Column(name = "target_id")
    private String targetId; // 目標ID (productId, orderId, etc.)

    @Column(name = "detail")
    private String detail; // 詳細資訊 (keyword, remark, etc.)

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "action_time")
    @Builder.Default
    private LocalDateTime actionTime = LocalDateTime.now();

    @PrePersist // 確保寫入前自動壓上時間
    protected void onCreate() {
        if (this.actionTime == null) {
            this.actionTime = LocalDateTime.now();
        }
    }
}
