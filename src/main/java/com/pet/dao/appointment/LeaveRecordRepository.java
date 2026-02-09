package com.pet.dao.appointment;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.appointment.LeaveRecord;


/**
 * LeaveRecordRepository: 請假紀錄存取層
 * 負責：請假單的基本 CRUD 操作。
 * 註：複雜查詢主要由 LeaveRecordGroomerViewRepository 負責。
 */
public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Integer> {

    /**
     * 檢查指定美容師是否有重疊的請假記錄
     * 重疊條件：新請假的開始日 <= 既有的結束日 AND 新請假的結束日 >= 既有的開始日
     */
    @Query("SELECT COUNT(lr) FROM LeaveRecord lr " +
           "WHERE lr.groomerId = :groomerId " +
           "AND lr.isActive = true " +
           "AND lr.startDate <= :endDate " +
           "AND lr.endDate >= :startDate")
    long countOverlappingLeaves(@Param("groomerId") Integer groomerId,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

}
