package com.pet.dao.appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.appointment.Groomer;

public interface GroomerRepository extends JpaRepository<Groomer, Integer> {

	Optional<Groomer> findByEmail(String Email);

	Optional<Groomer> findByPhone(String phone);
	
	@Query("SELECT g FROM Groomer g WHERE " +
	           "(:name IS NULL OR g.groomerName LIKE %:name%) AND " +
	           "(:startDate IS NULL OR g.hiredate >= :startDate) AND " +
	           "(:endDate IS NULL OR g.hiredate <= :endDate) AND " +
	           "(:isActive IS NULL OR g.isActive = :isActive)")
	    List<Groomer> complexSearch(
	            @Param("name") String name,
	            @Param("startDate") LocalDate startDate,
	            @Param("endDate") LocalDate endDate,
	            @Param("isActive") Boolean isActive
	    );


}
