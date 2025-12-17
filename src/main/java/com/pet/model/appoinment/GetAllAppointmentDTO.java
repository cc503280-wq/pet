package com.pet.model.appoinment;

import java.sql.Time;
import java.util.Date;

public class GetAllAppointmentDTO {
	private int appointmentId;
	private int memberId;
	private String memberName;
	private int petId;
	private String petName;
	private int serviceId;
	private String serviceName;
	private int employeeId;
	private String employeeName;
	private Date appointmentDate;
	private int slotId;
	private Time startTime;
	private Time endTime;
	private String notes;
	private int price;
	private String appointmentStatus;
	private String payStatus;
	private int rating;
	private String comment;
	private String reply;
	private Date updateTime;
	private int durationMinutes;
	
	public GetAllAppointmentDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetAllAppointmentDTO(int appointmentId, int memberId, String memberName, int petId, String petName,
			int serviceId, String serviceName, int employeeId, String employeeName, Date appointmentDate,
			Time startTime, Time endTime, String notes, int price, String appointmentStatus, String payStatus,
			int rating, String comment, String reply, Date updateTime,int slotId,int durationMinutes) {
		super();
		this.appointmentId = appointmentId;
		this.memberId = memberId;
		this.memberName = memberName;
		this.petId = petId;
		this.petName = petName;
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.appointmentDate = appointmentDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.notes = notes;
		this.price = price;
		this.appointmentStatus = appointmentStatus;
		this.payStatus = payStatus;
		this.rating = rating;
		this.comment = comment;
		this.reply = reply;
		this.updateTime = updateTime;
		this.slotId= slotId;
		this.durationMinutes = durationMinutes;
	}

	public int getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public int getPetId() {
		return petId;
	}

	public void setPetId(int petId) {
		this.petId = petId;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getAppointmentStatus() {
		return appointmentStatus;
	}

	public void setAppointmentStatus(String appointmentStatus) {
		this.appointmentStatus = appointmentStatus;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public int getSlotId() {
		return slotId;
	}

	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}

	public int getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	
	
	
	

}
