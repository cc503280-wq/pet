package com.pet.model.appointment;

import java.sql.Timestamp;

import java.util.Date;

import com.pet.model.member.MemberPet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity 
@Table(name="appointment")
public class Appointment {
	
	@Id @Column(name="appointment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer appointmentId;
	
	@Column(name = "pet_id", insertable= false, updatable = false)
    private Integer petId;
	@Column(name = "service_id", insertable= false, updatable = false)
    private Integer serviceId;
	@Column(name = "employee_id", insertable= false, updatable = false)
    private Integer employeeId;
	@Column(name = "slot_id", insertable= false, updatable = false)
    private Integer slotId;
	@Column(name="appointment_date")
    private Date appointmentDate;
	@Column(name="notes")
    private String notes;
	@Column(name="appointment_status")
    private String appointmentStatus; 
    
	@Column(name="Created_at")
    private Timestamp createdAt;
	@Column(name="Updated_at")
    private Timestamp updatedAt;

	@Column(name="Rating")
    private Integer rating;   
	@Column(name="Comment")
    private String comment;
	@Column(name="reply")
    private String reply;
	@Column(name="review_date")
    private Date reviewDate;
	@Column(name="total_price")
    private Integer totalPrice;
	@Column(name="pay_status")
    private String payStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_id")
	private PetService petservice;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private Employee employee;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "slot_id")
	private WorkSlot workSlot;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pet_id")
	private MemberPet memberPet;

	
	public Appointment() {
	}

	public Appointment(Integer appointmentId, Integer petId, Integer serviceId, Integer employeeId, Integer slotId,
			Date appointmentDate, String notes, String appointmentStatus, Timestamp createdAt,
			Timestamp updatedAt, Integer rating, String comment, String reply, Date reviewDate,
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

	public PetService getPetservice() {
		return petservice;
	}

	public void setPetservice(PetService petservice) {
		this.petservice = petservice;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public WorkSlot getWorkSlot() {
		return workSlot;
	}

	public void setWorkSlot(WorkSlot workSlot) {
		this.workSlot = workSlot;
	}

	public MemberPet getMemberPets() {
		return memberPet;
	}

	public void setMemberPets(MemberPet memberPet) {
		this.memberPet = memberPet;
	}

	

}
