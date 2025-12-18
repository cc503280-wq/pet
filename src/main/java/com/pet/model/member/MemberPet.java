package com.pet.model.member;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import javax.annotation.processing.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity @Table(name = "member_pets")
public class MemberPet {
	
	@Id @Column(name = "pet_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int petId;
	
	@ManyToOne
	@JoinColumn(name = "member_id", insertable = false, updatable = false)
	private Member member;

	@Column(name = "member_id")
	private int memberId;
	
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
	
	@Column(name = "created_at")
	private Timestamp createdAt;
	
	@Column(name = "updated_at")
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
