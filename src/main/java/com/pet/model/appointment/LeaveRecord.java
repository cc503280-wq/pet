package com.pet.model.appointment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "leave_record")
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_id")
    private Integer leaveId;

    @Column(name = "groomer_id", nullable = false)
    private Integer groomerId;

    @Column(name = "leave_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate leaveDate;


    @Column(nullable = false, length = 100)
    private String reason;
    
    @Column(name="is_active")
	private Boolean isActive;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne
	@JoinColumn(name = "groomer_id", insertable = false, updatable = false)
	private Groomer groomer;

}