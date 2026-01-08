package com.pet.model.appointment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.pet.model.member.MemberPet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointment")
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @Column(name = "pet_id")
    private Integer petId;

    @Column(name = "groomer_id")
    private Integer groomerId;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "notes")
    private String notes;

    @Column(name = "appointment_status", nullable = false, length = 20)
    private String appointmentStatus;

    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appointment", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude // 加上這行
    @ToString.Exclude
	private Set<AppointmentDetails> appointmentDetails = new HashSet<AppointmentDetails>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", insertable = false, updatable = false)
    private MemberPet memberPet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groomer_id", insertable = false, updatable = false) 
    private Groomer groomer;

	
    


	
}