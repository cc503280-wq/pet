package com.pet.model.appointment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class AppointmentRequest {
    private Integer memberId;
    private Integer petId;
    private Integer groomerId;
    private LocalDate appointmentDate; 
    private String startTime;       
    private String endTime;        
    private Integer durationMinutes;
    private BigDecimal totalPrice;
    private String notes;
    

    private List<Integer> serviceIds; 
}