package com.pet.dao.appointment;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.appointment.Appointment;

/**
 * AppointmentRepository: 預約資料存取層
 * 負責：查詢預約衝突、計算美容師剩餘班表、以及過期預約檢查
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // 計算特定美容師在 "今日之後" 還有多少筆「有效預約」
    // 用途：美容師離職檢查 (若有未來預約則須人工調整後才可離職)
	@Query("SELECT COUNT(a) FROM Appointment a WHERE a.groomerId = :groomerId " + "AND a.appointmentDate >= :today "
			+ "AND a.appointmentStatus != '已取消'")
	long countActiveFutureAppointments(@Param("groomerId") Integer groomerId, @Param("today") LocalDate today);


    // 計算特定美容師在 "日期範圍內" 的預約數量
    // 用途：請假檢查
	@Query("SELECT COUNT(a) FROM Appointment a WHERE a.groomerId = :groomerId AND a.appointmentDate BETWEEN :startDate AND :endDate")
	long countByGroomerIdAndDateRange(@Param("groomerId") Integer groomerId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

    // 計算特定服務項目在 "今日之後" 還有多少筆「有效預約」
    // 用途：服務下架檢查 (若被預約則不可下架)
	@Query("SELECT COUNT(a) FROM Appointment a " + "JOIN a.appointmentDetails d " + "WHERE d.serviceId = :serviceId "
			+ "AND a.appointmentDate >= :today " + "AND a.appointmentStatus != '已取消'")
	long countActiveAppointmentsByServiceId(@Param("serviceId") Integer serviceId, @Param("today") LocalDate today);

	/**
	 * 檢查某隻寵物在指定時段內是否重複預約 (已取消不在計算範圍內) 
     * 邏輯：(新開始 < 舊結束) AND (新結束 > 舊開始) = 時間重疊
	 */
	@Query(value = "SELECT COUNT(*) FROM appointment " + "WHERE pet_id = :petId " + "AND appointment_date = :date "
			+ "AND appointment_status != '已取消' "
			+ "AND (start_time < CAST(:endTime AS TIME) AND end_time > CAST(:startTime AS TIME))", nativeQuery = true)
	int countPetActiveAppointments(@Param("petId") Integer petId, @Param("date") LocalDate date,
			@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    // 查詢 "預約時間已過但未到場的預約
    // 用途：排程自動將過期預約標記為 "未到場"
    @Query(value = "SELECT * FROM appointment WHERE appointment_status = :status " +
           "AND (appointment_date < :today OR (appointment_date = :today AND start_time < CAST(:cutoffTime AS TIME)))", nativeQuery = true)
    java.util.List<Appointment> findExpiredAppointments(
        @Param("status") String status, 
        @Param("today") LocalDate today, 
        @Param("cutoffTime") LocalTime cutoffTime
    );

}
