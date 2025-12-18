package model;

import java.sql.Date;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="members")
public class Member {
	
	
	@Id @Column(name="member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberId;
	@Column(name="email")
    private String email;
	@Column(name="password")
    private String password;
	@Column(name="name")
    private String name;
	@Column(name="gender")
    private String gender; 
	@Column(name="birthday")
    private Date birthday;
	@Column(name="phone")
    private String phone;
	@Column(name="address")
    private String address;
	@Column(name="picture")
    private String picture;
	@Column(name="status")
    private String status;
	@Column(name="oauth_type")
    private String oauthType;
	@Column(name="oauth_id")
    private String oauthId;
	@Column(name="points")
    private Integer points;
	@Column(name="created_at")
    private Timestamp createdAt; 
	@Column(name="updated_at")
    private Timestamp updatedAt;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
	private Set<MemberPets> memberPets = new HashSet<MemberPets>();

	public Member() {
		
	}

	public Member(Integer memberId, String email, String password, String name, String gender, Date birthday,
			String phone, String address, String picture, String status, String oauthType, String oauthId,
			Integer points, Timestamp createdAt, Timestamp updatedAt) {
		this.memberId = memberId;
		this.email = email;
		this.password = password;
		this.name = name;
		this.gender = gender;
		this.birthday = birthday;
		this.phone = phone;
		this.address = address;
		this.picture = picture;
		this.status = status;
		this.oauthType = oauthType;
		this.oauthId = oauthId;
		this.points = points;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOauthType() {
		return oauthType;
	}

	public void setOauthType(String oauthType) {
		this.oauthType = oauthType;
	}

	public String getOauthId() {
		return oauthId;
	}

	public void setOauthId(String oauthId) {
		this.oauthId = oauthId;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
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

	public Set<MemberPets> getPet() {
		return memberPets;
	}

	public void setPet(Set<MemberPets> memberPets) {
		this.memberPets = memberPets;
	}

}
