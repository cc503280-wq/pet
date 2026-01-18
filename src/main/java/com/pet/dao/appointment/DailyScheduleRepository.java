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


/**
 * DailyScheduleRepository: 每日班表存取層
 * 負責：查詢可用時段、執行 Stored Procedure 產生班表、以及更新時段狀態
 */
public interface DailyScheduleRepository extends JpaRepository<DailySchedule, Integer> {
	
	// 查詢特定美容師某天的班表
	Optional<DailySchedule> findByGroomerIdAndWorkDate(Integer groomerId, LocalDate workDate);

    // 刪除指定區間的班表
    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM DailySchedule d WHERE d.groomerId = :groomerId AND d.workDate BETWEEN :startDate AND :endDate")
    void deleteByGroomerIdAndDateRange(@Param("groomerId") Integer groomerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 刪除特定美容師從某天開始的所有未來班表
    // 用途：美容師離職/停權後，清除未來排班
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM daily_schedule WHERE work_date >= :startDate AND groomer_id = :groomerId", 
           nativeQuery = true)
    void deleteFutureSchedulesByGroomer(LocalDate startDate, Integer groomerId);
    
    /**
     * 查詢所有「在職中」美容師在指定日期的班表
     * 回傳：time_slots (0011...), groomer_id, groomer_name
     * 用於：前端計算並顯示可預約空檔
     */
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
    
    /**
     * 呼叫 Stored Procedure 自動產生班表
     * @param groomerId 指定美容師 (若 null 則對所有美容師執行)
     * @param startDate 開始日期
     * @param days 產生天數 (如 120 天)
     * @param openTime 開始時間 (如 09:00)
     * @param closeTime 結束時間 (如 21:00)
     * @param weeklyOffDay 每週公休日 (0=週日, 1=週一... -1=無)
     */
    @Procedure(procedureName = "sp_GenerateGroomerSchedules")
    void generateGroomerSchedules(
        @Param("GroomerId") Integer groomerId,
        @Param("StartDate") LocalDate startDate,
        @Param("Days") Integer days,
        @Param("OpenTime") String openTime,
        @Param("CloseTime") String closeTime,
        @Param("WeeklyOffDay") Integer weeklyOffDay
    );
    
    
    
    // 直接更新時段字串 (time_slots) 
    // 用途：預約或請假時鎖定時段
    @Modifying
    @Transactional
    @Query(value = "UPDATE daily_schedule SET time_slots = :slots WHERE work_date = :date AND groomer_id = :groomerId", nativeQuery = true)
    void updateTimeSlots(@Param("date") LocalDate date, @Param("groomerId") Integer groomerId, @Param("slots") String slots);

}
