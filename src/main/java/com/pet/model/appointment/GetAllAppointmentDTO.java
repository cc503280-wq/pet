package com.pet.model.appointment;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class GetAllAppointmentDTO {
	private Integer appointmentId;
	private Integer memberId;
	private String memberName;
	private Integer petId;
	private String petName;
	private Integer serviceId;
	private String serviceName;
	private Integer employeeId;
	private String employeeName;
	private Date appointmentDate;
	private Integer slotId;
	private Time startTime;
	private Time endTime;
	private String notes;
	private Integer price;
	private String appointmentStatus;
	private String payStatus;
	private Integer rating;
	private String comment;
	private String reply;
	private Timestamp updateTime;
	private Integer durationMinutes;
	
	public GetAllAppointmentDTO() {
	}

	public GetAllAppointmentDTO(Integer appointmentId, Integer memberId, String memberName, Integer petId,
			String petName, Integer serviceId, String serviceName, Integer employeeId, String employeeName,
			Date appointmentDate, Integer slotId, Time startTime, Time endTime, String notes, Integer price,
			String appointmentStatus, String payStatus, Integer rating, String comment, String reply, Timestamp updateTime,
			Integer durationMinutes) {

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
		this.slotId = slotId;
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
		this.durationMinutes = durationMinutes;
	}

	public Integer getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(Integer appointmentId) {
		this.appointmentId = appointmentId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public Integer getPetId() {
		return petId;
	}

	public void setPetId(Integer petId) {
		this.petId = petId;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
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

	public Integer getSlotId() {
		return slotId;
	}

	public void setSlotId(Integer slotId) {
		this.slotId = slotId;
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

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
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

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	
	

	

}
