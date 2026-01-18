package com.pet.dao.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.appointment.AppointmentDetails;


/**
 * AppointmentDetailsRepository: 預約明細實體存取層
 * 用途：主要用於新增預約時，將選購的服務項目與預約單關聯儲存。
 */
public interface AppointmentDetailsRepository extends JpaRepository<AppointmentDetails, Integer> {
    // 繼承 JpaRepository 提供基本的 CRUD 功能
}
