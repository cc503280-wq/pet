package com.pet.model.appoinment;

import java.sql.Date;
import java.sql.Time;

public class MemberBean {
	
	// member_id INT IDENTITY(1,1) PRIMARY KEY
    private Integer memberId; 

    // email VARCHAR(255) UNIQUE
    private String email;

    // password VARCHAR(255)
    private String password;

    // name NVARCHAR(50)
    private String name;

    // gender CHAR(1)
    private String gender; // 使用 String 儲存 CHAR(1)

    // birthday DATE
    private Date birthday; // 建議使用 java.sql.Date 或 java.time.LocalDate

    // phone VARCHAR(20) UNIQUE
    private String phone;

    // address NVARCHAR(255)
    private String address;

    // picture VARCHAR(255)
    private String picture;

    // status VARCHAR(50) DEFAULT 'active'
    private String status;

    // oauth_type VARCHAR(20)
    private String oauthType;

    // oauth_id VARCHAR(255)
    private String oauthId;

    // points INT DEFAULT 0
    private Integer points;

    // created_at DATETIME
    private Time createdAt; 

    // updated_at DATETIME
    private Time updatedAt;

	public MemberBean() {
		super();
	}

	public MemberBean(Integer memberId, String email, String password, String name, String gender, Date birthday,
			String phone, String address, String picture, String status, String oauthType, String oauthId,
			Integer points, Time createdAt, Time updatedAt) {
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

	public Time getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Time createdAt) {
		this.createdAt = createdAt;
	}

	public Time getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Time updatedAt) {
		this.updatedAt = updatedAt;
	}

}
