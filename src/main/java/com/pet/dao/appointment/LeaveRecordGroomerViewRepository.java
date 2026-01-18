package com.pet.dao.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.appointment.LeaveRecoredGroomerView;

/**
 * LeaveRecordGroomerViewRepository: 請假班表存取層
 * 用途：供後台「請假審核」頁面查詢使用，透過View直接取得美容師名稱
 */
public interface LeaveRecordGroomerViewRepository extends JpaRepository<LeaveRecoredGroomerView, Integer> {

	/**
     * 複合條件查詢請假紀錄
     * 條件：美容師、請假區間、申請時間區間
     * 排序：開始日期 (新 -> 舊)
     */
	@Query("SELECT l FROM LeaveRecoredGroomerView l WHERE " +
	           "(:groomerId IS NULL OR l.groomerId = :groomerId) AND " +
	           "(:leaveStart IS NULL OR l.startDate >= :leaveStart) AND " +
	           "(:leaveEnd IS NULL OR l.endDate <= :leaveEnd) AND " +
	           "(:createStart IS NULL OR l.createdAt >= :createStart) AND " +
	           "(:createEnd IS NULL OR l.createdAt <= :createEnd) " +
	           "ORDER BY l.startDate DESC")
	    List<LeaveRecoredGroomerView> complexSearch(
	            @Param("groomerId") Integer groomerId,
	            @Param("leaveStart") LocalDate leaveStart,
	            @Param("leaveEnd") LocalDate leaveEnd,
	            @Param("createStart") LocalDateTime createStart,
	            @Param("createEnd") LocalDateTime createEnd
	    );

}
