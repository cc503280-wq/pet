package com.pet.model.appointment;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="schedule_block")
public class ScheduleBlock {
	
	@Id @Column(name="block_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer blockId;
	
	@Column(name="employee_id", insertable= false, updatable = false)
	private Integer employeeId;
	
	@Column(name = "slot_id", insertable= false, updatable = false)
	private Integer slotId;
	
	@Column(name = "block_date")
	private Date blockDate;
	@Column(name = "reason")
	private String reason;
	@Column(name="created_at")
    private Timestamp createdAt;
	@Column(name="updated_at")
    private Timestamp updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private Employee employee;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "slot_id")
	private WorkSlot workSlot;
	
	
	
	
	public ScheduleBlock() {
	}




	public ScheduleBlock(Integer employeeId, Integer slotId, Date blockDate, String reason, Timestamp createdAt,
			Timestamp updatedAt) {
		this.employeeId = employeeId;
		this.slotId = slotId;
		this.blockDate = blockDate;
		this.reason = reason;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}




	public Integer getBlockId() {
		return blockId;
	}




	public void setBlockId(Integer blockId) {
		this.blockId = blockId;
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




	public Date getBlockDate() {
		return blockDate;
	}




	public void setBlockDate(Date blockDate) {
		this.blockDate = blockDate;
	}




	public String getReason() {
		return reason;
	}




	public void setReason(String reason) {
		this.reason = reason;
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

}
