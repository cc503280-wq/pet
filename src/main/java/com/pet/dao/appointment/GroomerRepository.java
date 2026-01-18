package com.pet.dao.appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.appointment.Groomer;

/**
 * GroomerRepository: 美容師資料存取層
 * 負責：美容師的登入驗證、複合搜尋、以及工作人數統計
 */
public interface GroomerRepository extends JpaRepository<Groomer, Integer> {

    // 新增/修改資料時Email不能重複
    // Email登入使用
	Optional<Groomer> findByEmail(String Email);

    // 新增/修改資料時電話不能重複
	Optional<Groomer> findByPhone(String phone);
	
    /**
     * 美容師複合搜尋
     * @param name 姓名 (模糊查詢)
     * @param startDate 入職日 (起)
     * @param endDate 入職日 (迄)
     * @param isActive 是否在職
     */
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


    /**
     * 計算指定日期 "有上班" (未請假) 的美容師人數
     * 邏輯：所有在職美容師 (-) 扣除當天有請假紀錄的美容師 = 上班人數
     * 用途：請假與排班時的防呆，確保店內至少有 1 人值班。
     */
	@Query(value = """
            SELECT COUNT(*) FROM groomer g 
            WHERE g.is_active = 1 
            AND g.groomer_id NOT IN (
                SELECT lr.groomer_id FROM leave_record lr 
                WHERE :date BETWEEN lr.start_date AND lr.end_date 
                AND lr.is_active = 1
            )
            """, nativeQuery = true)
    long countWorkingGroomers(@Param("date") LocalDate date);

}
