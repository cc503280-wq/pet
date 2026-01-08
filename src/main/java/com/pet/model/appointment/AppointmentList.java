package com.pet.model.appointment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity 
@Table(name = "appointment_list_view")
public class AppointmentList {
	@Id
    @Column(name = "appointment_id")
	private Integer appointmentId;
	@Column(name = "member_name")
	private String memberName;
	@Column(name = "pet_name")
	private String petName;
	@Column(name = "main_service")
	private String mainService;
	@Column(name = "addon_items")
	private String addonItem;
	@Column(name = "groomer_name")
	private String groomerName;
	@Column(name = "appointment_date")
	private String appointmentDate;
	@Column(name = "start_time")
	private String startTime;
	@Column(name = "end_time")
	private String endTime;
	@Column(name = "duration_minutes")
	private Integer durationMinutes;
	@Column(name = "notes")
	private String notes;
	@Column(name = "appointment_status")
	private String appointmentStatus;
	@Column(name = "pet_type")
	private String petType;
	@Column(name = "pet_size")
	private String petSize;
	@Column(name = "phone")
	private String memberPhone;
	@Column(name = "final_price")
	private Integer price;
	@Column(name = "created_at")
	private String createdAt;
	@Column(name = "updated_at")
	private String updatedAt;

}
