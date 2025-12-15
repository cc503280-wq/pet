package bean;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Admin {
	private int adminId;
	private String email;
	private String password;
	private String name;
	private String phone;
	private String role;
	private String status;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	
	
	public Admin() {
		super();
	}

	
	


	public Admin(int adminId, String email, String name, String phone, String role, String status) {
		super();
		this.adminId = adminId;
		this.email = email;
		this.status = status;
		this.name = name;
		this.phone = phone;
		this.role = role;
	}


	public Admin(int adminId, String email, String password, String name, String phone, String role, String status,
			Timestamp createdAt, Timestamp updatedAt) {
		super();
		this.adminId = adminId;
		this.email = email;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.role = role;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Admin(String name, String email, String password, String phone, String role) {
		super();
	    this.name = name;
	    this.email = email;
	    this.password = password;
	    this.phone = phone;
	    this.role = role;
	    this.createdAt = Timestamp.valueOf(LocalDateTime.now());
	    this.updatedAt = Timestamp.valueOf(LocalDateTime.now());
	}
	




	public int getAdminId() {
		return adminId;
	}
	public void setAdminId(int adminId) {
		this.adminId = adminId;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
