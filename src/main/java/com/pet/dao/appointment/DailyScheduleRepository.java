package com.pet.dao.appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.pet.model.appointment.DailySchedule;

import jakarta.transaction.Transactional;


public interface DailyScheduleRepository extends JpaRepository<DailySchedule, Integer> {
	
	
	Optional<DailySchedule> findByGroomerIdAndWorkDate(Integer groomerId, LocalDate workDate);
	
	@Modifying
    @Transactional
    @Query(value = "DELETE FROM daily_schedule WHERE work_date = :date AND groomer_id = :groomerId", 
           nativeQuery = true)
    void deleteByWorkDateAndGroomerId(LocalDate date, Integer groomerId);

    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM daily_schedule WHERE work_date >= :startDate AND groomer_id = :groomerId", 
           nativeQuery = true)
    void deleteFutureSchedulesByGroomer(LocalDate startDate, Integer groomerId);
    
    
    @Query(value = """
            SELECT 
                ds.schedule_id AS scheduleId,
                ds.time_slots  AS timeSlots,
                e.groomer_id   AS groomerId,
                e.groomer_name AS name
            FROM 
                daily_schedule AS ds 
            INNER JOIN 
                groomer AS e ON ds.groomer_id = e.groomer_id 
            WHERE 
                ds.work_date = :date 
                AND e.is_active = 1
        """, nativeQuery = true)
        List<Map<String, Object>> findSchedulesByDate(@Param("date") LocalDate date);
    
    @Procedure(procedureName = "sp_GenerateGroomerSchedules")
    void generateGroomerSchedules(
        @Param("GroomerId") Integer groomerId,
        @Param("StartDate") LocalDate startDate,
        @Param("Days") Integer days,
        @Param("OpenTime") String openTime,
        @Param("CloseTime") String closeTime,
        @Param("WeeklyOffDay") Integer weeklyOffDay
    );
    
    @Modifying
    @Query("DELETE FROM DailySchedule d WHERE d.groomerId = :groomerId AND d.workDate BETWEEN :startDate AND :endDate")
    void deleteByGroomerIdAndDateRange(@Param("groomerId") Integer groomerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    

}
