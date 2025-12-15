package bean;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class Member {
	
	private int memberId;
	private String email;
	private String password;
	private String name;
	private String gender;
	private Date birthday;
	private String phone;
	private String address;
	private String picture;
	private String status;
	private String oauthType;
	private String oauthId;
	private int points;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	
	public Member() {
		super();
	}

	
	
	public Member(int memberId, String name) {
		super();
		this.memberId = memberId;
		this.name = name;
	}



	public Member(int memberId, String email, String password, String name, String gender, Date birthday, String phone,
			String address, String picture, String status, String oauthType, String oauthId, int points,
			Timestamp createdAt, Timestamp updatedAt) {
		super();
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
	
	

	public Member(String email, String password, String name, String gender, Date birthday, String phone,
			String address, String picture) {
		super();
		this.email = email;
		this.password = password;
		this.name = name;
		this.gender = gender;
		this.birthday = birthday;
		this.phone = phone;
		this.address = address;
		this.picture = picture;
	}
	
	public Member(int memberId ,String email, String name, String gender, Date birthday, String phone,
			String address, String picture) {
		super();
		this.memberId = memberId;
		this.email = email;
		this.name = name;
		this.gender = gender;
		this.birthday = birthday;
		this.phone = phone;
		this.address = address;
		this.picture = picture;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
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

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
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
	
	
}
