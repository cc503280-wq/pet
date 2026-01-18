package com.pet.dao.appointment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.pet.model.appointment.AppointmentList;

/**
 * AppointmentListRepository: 預約列表視圖 Repository
 * 用途：專門用於後台查詢預約列表，包含複雜的多條件搜尋功能。
 * 繼承 JpaRepository 針對 AppointmentList (View 實體) 進行唯讀查詢。
 */
@Repository
public interface AppointmentListRepository extends JpaRepository<AppointmentList, Integer>{
	
    // 依會員 ID 查詢
	List<AppointmentList> findByMemberId(String memberId);
	
    // 依美容師 ID 查詢
	List<AppointmentList> findByGroomerId(String groomerId);

    /**
     * 複合條件搜尋 (動態條件)
     */
	@Query("SELECT a FROM AppointmentList a WHERE " +
	           "(:phone IS NULL OR :phone = '' OR a.memberPhone LIKE CONCAT('%', :phone, '%')) AND " +
	           "(:status IS NULL OR :status = '' OR a.appointmentStatus = :status) AND " +
	           "(:startDate IS NULL OR :startDate = '' OR a.appointmentDate >= :startDate) AND " +
	           "(:endDate IS NULL OR :endDate = '' OR a.appointmentDate <= :endDate) AND " +
	           "(:groomerId IS NULL OR :groomerId = '' OR a.groomerId = :groomerId) AND " +
	           "(:createdAtStart IS NULL OR :createdAtStart = '' OR a.createdAt >= :createdAtStart) AND " +
	           "(:createdAtEnd IS NULL OR :createdAtEnd = '' OR a.createdAt <= :createdAtEnd)")
	    List<AppointmentList> complexSearch(
	            @Param("phone") String phone, 
	            @Param("status") String status,
	            @Param("startDate") String startDate,
	            @Param("endDate") String endDate,
	            @Param("groomerId") String groomerId,
	            @Param("createdAtStart") String createdAtStart,
	            @Param("createdAtEnd") String createdAtEnd);

	
}
