package com.pet.model.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DailySchedule: 每日排班實體類 (對應資料庫 daily_schedule 表)
 * 儲存美容師某一天的排班狀況與時段佔用情形。
 */
@Entity
@Table(name = "daily_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId; 

    @Column(name = "groomer_id")
    private Integer groomerId; 
    
    @Column(name = "time_slots")
    private String timeSlots; 
   
    /**
     * scheduleVersion: 樂觀鎖版本號 (Optimistic Locking)
     * 用途：防止併發預約衝突。當多個請求同時嘗試修改同一天的班表時，
     * JPA 會檢查版本號，版本不符者會拋出 ObjectOptimisticLockingFailureException。
     */
    @Version
    @Column(name = "schedule_version")
    private Integer scheduleVersion = 1;

    @Column(name = "work_date")
    private LocalDate workDate; 

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne
	@JoinColumn(name = "groomer_id", insertable = false, updatable = false)
	private Groomer groomer;
}
