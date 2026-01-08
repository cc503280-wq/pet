package com.pet.dao.appointment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.pet.model.appointment.AppointmentList;

@Repository
public interface AppointmentListRepository extends JpaRepository<AppointmentList, Integer>{
	

	@Query("SELECT a FROM AppointmentList a WHERE " +
	           "(:phone IS NULL OR :phone = '' OR a.memberPhone = :phone) AND " +
	           "(:status IS NULL OR :status = '' OR a.appointmentStatus = :status) AND " +
	           "(:startDate IS NULL OR :startDate = '' OR a.appointmentDate >= :startDate) AND " +
	           "(:endDate IS NULL OR :endDate = '' OR a.appointmentDate <= :endDate)")
	    List<AppointmentList> complexSearch(
	            @Param("phone") String phone, 
	            @Param("status") String status,
	            @Param("startDate") String startDate,
	            @Param("endDate") String endDate);

	
}
