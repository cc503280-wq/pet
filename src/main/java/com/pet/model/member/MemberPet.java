package com.pet.model.member;

import java.time.LocalDateTime;


import com.fasterxml.jackson.annotation.JsonFormat;

//import com.pet.model.appointment.Appointment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "member_pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberPet {
	
	@Id @Column(name = "pet_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer petId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", insertable = false, updatable = false)
	private Member member;

	@Column(name = "pet_name")
	private String petName;
	
	@Column(name = "pet_type")
	private String petType;
	
	@Column(name = "pet_breed")
	private String petBreed;
	
	@Column(name = "pet_age")
	private String petAge;
	
	@Column(name = "pet_size")
	private String petSize;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
