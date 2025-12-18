package com.pet.model.appointment;

import java.sql.Time;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="work_slot")
public class WorkSlot {
	@Id @Column(name="slot_id")
	private Integer slotId;
	@Column(name="start_time")
	private Time startTime;
	@Column(name="end_time")
	private Time endTime;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "workSlot")
	private Set<Appointment> appointments = new HashSet<Appointment>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "workSlot")
	private Set<ScheduleBlock> block = new HashSet<ScheduleBlock>();

	public WorkSlot() {
		
	}

	public WorkSlot(Time startTime, Time endTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
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

	public Set<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(Set<Appointment> appointments) {
		this.appointments = appointments;
	}

	public Set<ScheduleBlock> getBlock() {
		return block;
	}

	public void setBlock(Set<ScheduleBlock> block) {
		this.block = block;
	}

}
