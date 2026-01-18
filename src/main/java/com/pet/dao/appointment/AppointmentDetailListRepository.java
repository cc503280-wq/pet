package com.pet.dao.appointment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.appointment.AppointmentDetailList;

/**
 * AppointmentDetailListRepository: 預約明細列表視圖 Repository
 * 用途：主要用於後台「預約明細」的查詢 (唯讀)
 */
public interface AppointmentDetailListRepository extends JpaRepository<AppointmentDetailList, Integer>  {
    // 根據預約單 ID 查詢該單的所有明細 (包含服務名稱與價格)
	List<AppointmentDetailList> findByAppointmentId(Integer appointmentId);
}
