package com.pet.model.appointment;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
import lombok.NoArgsConstructor;


@Entity
@Table(name = "groomer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Groomer {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "groomer_id")
    private Integer groomerId;


    @Column(name = "groomer_name", nullable = false, length = 10)
    private String groomerName;

    @Column(nullable = false, length = 30, unique = true)
    private String phone;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate hiredate;

    @Column(name = "picture")
    private String picture;
    

    @Column(name = "is_active")
    private Boolean isActive = true;  
   
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
    
 
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groomer", cascade = CascadeType.ALL)
    @JsonIgnore
	private Set<LeaveRecord> leaveRecords = new HashSet<LeaveRecord>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groomer", cascade = CascadeType.ALL)
    @JsonIgnore
	private Set<DailySchedule> dailySchedules = new HashSet<DailySchedule>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groomer", cascade = CascadeType.ALL)
    @JsonIgnore
	private Set<Appointment> appointments = new HashSet<Appointment>();

	

}
