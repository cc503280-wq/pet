package com.pet.model.appointment;

import java.sql.Date;
import java.sql.Time;



public class EmployeeScheduleOverviewDTO {
		private Integer slot_id;
		private Integer employeeId;
		private String employeeName;
		private Time startTime; 
		private Time endTime;
		private String slotStatus;
		private String detailInfo;
    
		private Date targetDate;

		public EmployeeScheduleOverviewDTO() {	
		}

		public EmployeeScheduleOverviewDTO(Integer slot_id, Integer employeeId, String employeeName, Time startTime,
				Time endTime, String slotStatus, String detailInfo) {
			this.slot_id = slot_id;
			this.employeeId = employeeId;
			this.employeeName = employeeName;
			this.startTime = startTime;
			this.endTime = endTime;
			this.slotStatus = slotStatus;
			this.detailInfo = detailInfo;
		
		}

		public Integer getSlot_id() {
			return slot_id;
		}

		public void setSlot_id(Integer slot_id) {
			this.slot_id = slot_id;
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

		public String getSlotStatus() {
			return slotStatus;
		}

		public void setSlotStatus(String slotStatus) {
			this.slotStatus = slotStatus;
		}

		public String getDetailInfo() {
			return detailInfo;
		}

		public void setDetailInfo(String detailInfo) {
			this.detailInfo = detailInfo;
		}

		public Date getTargetDate() {
			return targetDate;
		}

		public void setTargetDate(Date targetDate) {
			this.targetDate = targetDate;
		}
	    
		
}

	
		
		
