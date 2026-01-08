package com.pet.dao.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.appointment.LeaveRecoredGroomerView;

public interface LeaveRecordGroomerViewRepository extends JpaRepository<LeaveRecoredGroomerView, Integer> {

	
	@Query("SELECT l FROM LeaveRecoredGroomerView l WHERE " +
	           "(:groomerId IS NULL OR l.groomerId = :groomerId) AND " +
	           "(:leaveStart IS NULL OR l.leaveDate >= :leaveStart) AND " +
	           "(:leaveEnd IS NULL OR l.leaveDate <= :leaveEnd) AND " +
	           "(:createStart IS NULL OR l.createdAt >= :createStart) AND " +
	           "(:createEnd IS NULL OR l.createdAt <= :createEnd) " +
	           "ORDER BY l.leaveDate DESC")
	    List<LeaveRecoredGroomerView> complexSearch(
	            @Param("groomerId") Integer groomerId,
	            @Param("leaveStart") LocalDate leaveStart,
	            @Param("leaveEnd") LocalDate leaveEnd,
	            @Param("createStart") LocalDateTime createStart,
	            @Param("createEnd") LocalDateTime createEnd
	    );

}
