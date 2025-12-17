package com.pet.model.member;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class MemberPet {
	
	private int petId;
	private int memberId;
	private String petName;
	private String petType;
	private String petBreed;
	private String petAge;
	private String petSize;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	
	public MemberPet() {
		super();
	}

	public MemberPet(int petId, int memberId, String petName, String petType, String petBreed, String petAge,
			String petSize, Timestamp createdAt, Timestamp updatedAt) {
		super();
		this.petId = petId;
		this.memberId = memberId;
		this.petName = petName;
		this.petType = petType;
		this.petBreed = petBreed;
		this.petAge = petAge;
		this.petSize = petSize;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public int getPetId() {
		return petId;
	}

	public void setPetId(int petId) {
		this.petId = petId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
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

	public String getPetBreed() {
		return petBreed;
	}

	public void setPetBreed(String petBreed) {
		this.petBreed = petBreed;
	}

	public String getPetAge() {
		return petAge;
	}

	public void setPetAge(String petAge) {
		this.petAge = petAge;
	}

	public String getPetSize() {
		return petSize;
	}

	public void setPetSize(String petSize) {
		this.petSize = petSize;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
	
}
