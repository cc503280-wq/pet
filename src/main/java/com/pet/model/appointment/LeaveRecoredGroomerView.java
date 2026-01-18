package com.pet.model.appointment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Immutable;



@Data
@Table(name = "leave_record_groomer_view")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable // 唯讀視圖
public class LeaveRecoredGroomerView {
	@Id
    @Column(name = "leave_id")
    private Integer leaveId; 

    @Column(name = "groomer_id")
    private Integer groomerId; 
    
    @Column(name = "groomer_name")
    private String groomerName; 

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
    
}