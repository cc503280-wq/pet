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
@Table(name="employee")
public class Employee {
	
	@Id @Column(name="employee_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer employeeId;
	@Column(name="ename")
	private String ename;
	@Column(name="phone")
    private String phone;
	@Column(name="email")
    private String email;
	@Column(name="hiredate")
    private Date hiredate;
	@Column(name="profile_photo")
    private String profilePhoto; 
	@Column(name="is_active")
    private Boolean isActive;     
	@Column(name="created_at")
    private Timestamp createdAt;
	@Column(name="updated_at")
    private Timestamp updatedAt;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "employee", cascade = CascadeType.ALL)
	private Set<Appointment> appointments = new HashSet<Appointment>();
    
    public Employee() {
		super();
	}
    
    public Employee(Integer employeeId, String ename, String phone, String email, Date hiredate,
			String profilePhoto, Boolean isActive, Timestamp createdAt, Timestamp updatedAt) {
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

	public Set<Appointment> getAppointment() {
		return appointments;
	}

	public void setAppointment(Set<Appointment> appointments) {
		appointments = appointments;
	}

}
