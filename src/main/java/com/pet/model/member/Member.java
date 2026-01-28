package com.pet.model.member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
	
	@Id @Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memberId;
	
	@Column(unique = true)
	private String email;
	
	@Column(nullable = true)
	private String password;
	
	private String name;
	
	private String gender;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthday;
	
	@Column(unique = true)
	private String phone;
	
	private String address;
	
	private String picture;
	
	private String status;
	
	@Column(name = "google_id")
	private String googleId;
	
	@Column(name = "line_id")
	private String lineId;
	
	private Integer points;
	
	@Column(name = "created_at",updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@JsonIgnore //<== 這是我新增的，我將此變唯讀，需跟組長討論
	private List<MemberPet> pets;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL) // mappedBy 對應 ChatMessage 裡的 "member" 屬性名
    @JsonIgnore 
    private List<ChatMessage> chatMessages;
	
    @PrePersist
    protected void onCreate() {
    	LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        	// 補上預設值
     		if (this.status == null) {
     			this.status = "active";
     		}
     		if (this.points == null) {
     			this.points = 0;
     		}

    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
}
