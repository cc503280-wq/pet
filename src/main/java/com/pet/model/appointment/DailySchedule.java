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

    @Column(name = "groomer_id", nullable = false)
    private Integer groomerId;
    
    @Column(name = "time_slots", nullable = false, length = 96)
    private String timeSlots; 
   
    @Column(name = "schedule_version", nullable = false)
    private Integer scheduleVersion = 1;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne
	@JoinColumn(name = "groomer_id", insertable = false, updatable = false)
	private Groomer groomer;

	

	
}
