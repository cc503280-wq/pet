package com.pet.dao.appointment;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.appointment.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

	@Query("SELECT COUNT(a) FROM Appointment a WHERE a.groomerId = :groomerId " + "AND a.appointmentDate >= :today "
			+ "AND a.appointmentStatus != '已取消'")
	long countActiveFutureAppointments(@Param("groomerId") Integer groomerId, @Param("today") LocalDate today);

	@Query("SELECT COUNT(a) FROM Appointment a WHERE a.groomerId = :groomerId " + "AND a.appointmentDate = :leaveDate "
			+ "AND a.appointmentStatus != '已取消'")
	long countByGroomerIdAndAppointmentDate(@Param("groomerId") Integer groomerId,
			@Param("leaveDate") LocalDate leaveDate);

	@Query("SELECT COUNT(a) FROM Appointment a WHERE a.groomerId = :groomerId AND a.appointmentDate BETWEEN :startDate AND :endDate")
	long countByGroomerIdAndDateRange(@Param("groomerId") Integer groomerId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query("SELECT COUNT(a) FROM Appointment a " + "JOIN a.appointmentDetails d " + "WHERE d.serviceId = :serviceId "
			+ "AND a.appointmentDate >= :today " + "AND a.appointmentStatus != '已取消'")
	long countActiveAppointmentsByServiceId(@Param("serviceId") Integer serviceId, @Param("today") LocalDate today);

	/**
	 * 檢查某隻寵物在指定時段內是否已有預約 (排除已取消的) 邏輯：(新開始 < 舊結束) AND (新結束 > 舊開始) = 時間重疊
	 */
	@Query(value = "SELECT COUNT(*) FROM appointment " + "WHERE pet_id = :petId " + "AND appointment_date = :date "
			+ "AND appointment_status != '已取消' "
			+ "AND (start_time < CAST(:endTime AS TIME) AND end_time > CAST(:startTime AS TIME))", nativeQuery = true)
	int countPetActiveAppointments(@Param("petId") Integer petId, @Param("date") LocalDate date,
			@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

}
