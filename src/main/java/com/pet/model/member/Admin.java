package com.pet.model.member;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {
	
	@Id @Column(name = "admin_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int adminId;
	
	@Column(unique = true)
	private String email;
	
	private String password;
	
	private String name;
	
	private String phone;
	
	private String role;
	
	private String status;
	
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
		
		// 預設管理員狀態
		if (this.status == null) {
			this.status = "active";
		}
		
		// 預設角色 (如果沒給就給最一般的管理員)
		if (this.role == null) {
			this.role = "admin";
		}
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
