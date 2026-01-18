package com.pet.model.appointment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
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

    @Column(name = "groomer_id")
    private Integer groomerId;

    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; 

    @Column(name = "end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate; 

    @Column(name ="reason")
    private String reason; 
    
    @Column(name="is_active")
	private Boolean isActive; 

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime updatedAt;
    
    @ManyToOne
	@JoinColumn(name = "groomer_id", insertable = false, updatable = false)
	private Groomer groomer;

}