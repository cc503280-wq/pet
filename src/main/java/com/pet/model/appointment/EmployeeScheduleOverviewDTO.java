package com.pet.model.appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public class EmployeeScheduleOverviewDTO {
		private Integer employeeId;
	 	private LocalDate targetDate;       
	    private String employeeName;        
	    private LocalTime startTime;        
	    private LocalTime endTime;          
	    private String slotStatus;         
	    private String detailInfo;
	    private Integer slot_id;
	    
		public EmployeeScheduleOverviewDTO() {
		}
		
		public EmployeeScheduleOverviewDTO(Integer employeeId, LocalDate targetDate, String employeeName, LocalTime startTime,
				LocalTime endTime, String slotStatus, String detailInfo, Integer slot_id) {
			this.employeeId = employeeId;
			this.targetDate = targetDate;
			this.employeeName = employeeName;
			this.startTime = startTime;
			this.endTime = endTime;
			this.slotStatus = slotStatus;
			this.detailInfo = detailInfo;
			this.slot_id = slot_id;
		}
		
		public EmployeeScheduleOverviewDTO(
	            Integer slot_id, 
	            Integer employeeId, 
	            String employeeName, 
	            LocalTime startTime, 
	            LocalTime endTime, 
	            String slotStatus, 
	            String detailInfo) {
	        this.slot_id = slot_id;
	        this.employeeId = employeeId;
	        this.employeeName = employeeName;
	        this.startTime = startTime;
	        this.endTime = endTime;
	        this.slotStatus = slotStatus;
	        this.detailInfo = detailInfo;
	    }
		
		
		

		public Integer getEmployeeId() {
			return employeeId;
		}

		public void setEmployeeId(Integer employeeId) {
			this.employeeId = employeeId;
		}

		public LocalDate getTargetDate() {
			return targetDate;
		}

		public void setTargetDate(LocalDate targetDate) {
			this.targetDate = targetDate;
		}

		public String getEmployeeName() {
			return employeeName;
		}

		public void setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
		}

		public LocalTime getStartTime() {
			return startTime;
		}

		public void setStartTime(LocalTime startTime) {
			this.startTime = startTime;
		}

		public LocalTime getEndTime() {
			return endTime;
		}

		public void setEndTime(LocalTime endTime) {
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

		public Integer getSlot_id() {
			return slot_id;
		}

		public void setSlot_id(Integer slot_id) {
			this.slot_id = slot_id;
		}      

}
