package com.pet.model.appointment;

public class AppointSuccessDTO {
	private Integer appointmentId;
	private String memberName;
	private String petName;
	private String serviceName;
	private String price;
	private String duration;
	private String notes;
	private String empName;
	private String appointmentDateStr;
	private String appointmentstartTime;
	private String appointmentendTime;
	private String appointmentStatus;
	private String payStatus;
	private String durationMinutes;
	private String startTime;
	private String rating;
	private String comment;
	private String reply;
	
	public AppointSuccessDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AppointSuccessDTO(String memberName, String petName, String serviceName, String price, String duration,
			String notes, String empName, String appointmentDateStr,Integer appointmentId, String appointmentstartTime,String appointmentendTime ,String appointmentStatus,String payStatus, String durationMinutes, String startTime,String rating,String comment,String reply) {
		super();
		this.memberName = memberName;
		this.petName = petName;
		this.serviceName = serviceName;
		this.price = price;
		this.duration = duration;
		this.notes = notes;
		this.empName = empName;
		this.appointmentDateStr = appointmentDateStr;
		this.appointmentId = appointmentId;
		this.appointmentstartTime =appointmentstartTime;
		this.appointmentendTime =appointmentendTime;
		this.appointmentStatus = appointmentStatus;
		this.payStatus = payStatus;
		this.durationMinutes =durationMinutes;
		this.rating=rating;
		this.reply=reply;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getAppointmentDateStr() {
		return appointmentDateStr;
	}

	public void setAppointmentDateStr(String appointmentDateStr) {
		this.appointmentDateStr = appointmentDateStr;
	}

	public Integer getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(Integer appointmentId) {
		this.appointmentId = appointmentId;
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

	public String getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(String durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
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

	public String getAppointmentstartTime() {
		return appointmentstartTime;
	}

	public void setAppointmentstartTime(String appointmentstartTime) {
		this.appointmentstartTime = appointmentstartTime;
	}

	public String getAppointmentendTime() {
		return appointmentendTime;
	}

	public void setAppointmentendTime(String appointmentendTime) {
		this.appointmentendTime = appointmentendTime;
	}

	
	
	
	

}
