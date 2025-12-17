package com.pet.model.appoinment;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberPetsBean {
	
	private Integer petId;
    // private Integer memberId; 
    private String petName;
    private String petType;
    private LocalDate petBirthday; // 對應 pet_birthday
    private String petSize;       // 有 CHECK ('小型','中型','大型','巨型') 約束
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
	public MemberPetsBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MemberPetsBean(Integer petId, String petName, String petType, LocalDate petBirthday, String petSize,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.petId = petId;
		this.petName = petName;
		this.petType = petType;
		this.petBirthday = petBirthday;
		this.petSize = petSize;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Integer getPetId() {
		return petId;
	}

	public void setPetId(Integer petId) {
		this.petId = petId;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public String getPetType() {
		return petType;
	}

	public void setPetType(String petType) {
		this.petType = petType;
	}

	public LocalDate getPetBirthday() {
		return petBirthday;
	}

	public void setPetBirthday(LocalDate petBirthday) {
		this.petBirthday = petBirthday;
	}

	public String getPetSize() {
		return petSize;
	}

	public void setPetSize(String petSize) {
		this.petSize = petSize;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
