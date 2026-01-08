package com.pet.dao.appointment;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.appointment.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer>{

	@Query("SELECT COUNT(a) FROM Appointment a WHERE a.groomerId = :groomerId " +
	           "AND a.appointmentDate >= :today " +
	           "AND a.appointmentStatus != '已取消'")
	    long countActiveFutureAppointments(@Param("groomerId") Integer groomerId, 
	                                      @Param("today") LocalDate today);
	
	
	@Query("SELECT COUNT(a) FROM Appointment a WHERE a.groomerId = :groomerId " +
	           "AND a.appointmentDate = :leaveDate " +
	           "AND a.appointmentStatus != '已取消'")
	    long countByGroomerIdAndAppointmentDate(@Param("groomerId") Integer groomerId, 
	                                      @Param("leaveDate") LocalDate leaveDate);
	
	@Query("SELECT COUNT(a) FROM Appointment a WHERE a.groomerId = :groomerId AND a.appointmentDate BETWEEN :startDate AND :endDate")
    long countByGroomerIdAndDateRange(@Param("groomerId") Integer groomerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
	

}
