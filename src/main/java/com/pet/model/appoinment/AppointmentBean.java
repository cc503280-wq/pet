package com.pet.model.appoinment;
import java.sql.Date;

public class AppointmentBean {
	
	private Integer appointmentId;
    
    private Integer petId;
    private Integer serviceId;
    private Integer employeeId;
    private Integer slotId;

    private Date appointmentDate;
    private String notes;
    private String appointmentStatus; 
    
    // 時間戳
    private Date createdAt;
    private Date updatedAt;

 
    private Integer rating;         
    private String comment;
    private String reply;
    private Date reviewDate;
    
    private Integer totalPrice;
    private String payStatus;

	public AppointmentBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AppointmentBean(Integer appointmentId, Integer petId, Integer serviceId, Integer employeeId, Integer slotId,
			Date appointmentDate, String notes, String appointmentStatus, Date createdAt,
			Date updatedAt, Integer rating, String comment, String reply, Date reviewDate,
			Integer totalPrice,String payStatus) {
		super();
		this.appointmentId = appointmentId;
		this.petId = petId;
		this.serviceId = serviceId;
		this.employeeId = employeeId;
		this.slotId = slotId;
		this.appointmentDate = appointmentDate;
		this.notes = notes;
		this.appointmentStatus = appointmentStatus;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.rating = rating;
		this.comment = comment;
		this.reply = reply;
		this.reviewDate = reviewDate;
		this.totalPrice = totalPrice;
		this.payStatus = payStatus;
		
	}

	public Integer getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(Integer appointmentId) {
		this.appointmentId = appointmentId;
	}

	public Integer getPetId() {
		return petId;
	}

	public void setPetId(Integer petId) {
		this.petId = petId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getSlotId() {
		return slotId;
	}

	public void setSlotId(Integer slotId) {
		this.slotId = slotId;
	}

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getAppointmentStatus() {
		return appointmentStatus;
	}

	public void setAppointmentStatus(String appointmentStatus) {
		this.appointmentStatus = appointmentStatus;
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

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public Integer getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	

}
