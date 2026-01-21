package com.pet.model.member;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "couponUsers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUsersReal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "coupon_id")
    private Integer couponId;

    @NonNull
    private String status;

    @Column(name = "assigned_at")
    private LocalDate assignedAt;

    @NonNull
    @Column(name = "used_at")
    private LocalDate usedAt;

    @PrePersist
    protected void onCreate() {
        this.assignedAt = LocalDateTime.now();
    }
}
