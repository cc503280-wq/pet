package model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
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

@Entity 
@Table(name="member_pets")
public class MemberPets {
	
	@Id @Column(name="pet_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer petId;
	@Column(name = "member_id", insertable = false, updatable = false)
	private Integer memberId;
	@Column(name="pet_name")
    private String petName;
	@Column(name="pet_type")
    private String petType;
	@Column(name="pet_age")
    private String petAge; 
	@Column(name="pet_breed")
	private String petBreed;
	@Column(name="pet_size")
    private String petSize;       
	@Column(name="created_at")
    private Timestamp createdAt;
	@Column(name="updated_at")
    private Timestamp updatedAt;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "memberPets", cascade = CascadeType.ALL)
	private Set<Appointment> appointments = new HashSet<Appointment>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id") 
	private Member member;
    
	public MemberPets() {
	}

	public MemberPets(Integer petId,Integer memberId, String petName, String petType, String petAge, String petBreed, String petSize,
			Timestamp createdAt, Timestamp updatedAt) {
		this.petId = petId;
		this.memberId = memberId;
		this.petName = petName;
		this.petType = petType;
		this.petAge = petAge;
		this.petBreed = petBreed;
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

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
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

	public String getPetAge() {
		return petAge;
	}

	public void setPetAge(String petAge) {
		this.petAge = petAge;
	}

	public String getPetBreed() {
		return petBreed;
	}

	public void setPetBreed(String petBreed) {
		this.petBreed = petBreed;
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
