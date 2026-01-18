package com.pet.dao.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.appointment.LeaveRecord;


/**
 * LeaveRecordRepository: 請假紀錄存取層
 * 負責：請假單的基本 CRUD 操作。
 * 註：複雜查詢主要由 LeaveRecordGroomerViewRepository 負責。
 */
public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Integer> {

}
