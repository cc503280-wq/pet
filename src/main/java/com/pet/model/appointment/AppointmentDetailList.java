package com.pet.model.appointment;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="appointment_detail_list_view")
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDetailList {
	
	@Id
    @Column(name = "detail_id")
    private Integer detailId; 

    @Column(name = "appointment_id")
    private Integer appointmentId; 

    @Column(name = "service_id")
    private Integer serviceId; 
    
    @Column(name = "service_name")
    private String serviceName; 

    @Column(name="price")
    private Integer price; 
    
    @Column(name="is_addon")
	private Boolean isAddon; 

    @Column(name = "duration_minutes")
    private Integer durationMinutes; 

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime createdAt;

}
