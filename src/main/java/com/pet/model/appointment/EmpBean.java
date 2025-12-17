package com.pet.model.appointment;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmpBean {
	
	private Integer employeeId;
	private String ename;
    private String phone;
    private String email;
    private Date hiredate;
    private String profilePhoto; // 對應 profile_photo
    private Boolean isActive;     // 對應 is_active
    private Date createdAt;
    private Date updatedAt;
    
    public EmpBean() {
		super();
	}
    
    public EmpBean(Integer employeeId, String ename, String phone, String email, Date hiredate,
			String profilePhoto, Boolean isActive, Date createdAt, Date updatedAt) {
		super();
		this.employeeId = employeeId;
		this.ename = ename;
		this.phone = phone;
		this.email = email;
		this.hiredate = hiredate;
		this.profilePhoto = profilePhoto;
		this.isActive = isActive;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
    
	public Integer getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getHiredate() {
		return hiredate;
	}
	public void setHiredate(Date hiredate) {
		this.hiredate = hiredate;
	}
	public String getProfilePhoto() {
		return profilePhoto;
	}
	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

}
