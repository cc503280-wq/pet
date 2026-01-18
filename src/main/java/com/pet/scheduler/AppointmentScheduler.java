package com.pet.scheduler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pet.common.AppConstants;
import com.pet.dao.appointment.AppointmentRepository;
import com.pet.model.appointment.Appointment;

/**
 * AppointmentScheduler: 預約相關的定時任務排程器
 * 負責：自動掃描並更新過期預約的狀態 (如：逾期未到)。
 */
@Component
public class AppointmentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentScheduler.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    /**
     * 自動更新過期預約為「未到店 (No Show)」
     * 頻率：每 10 分鐘執行一次 (Cron: 0 *\/10 * * * ?)
     * 條件：狀態為 "已確認 (Confirmed)" 且 "當前時間已超過預約開始時間 30 分鐘"。
     */
    @Scheduled(cron = "0 */10 * * * ?")
    @Transactional
    public void updateNoShowAppointments() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime cutoffTime;

        // 邏輯處理：防止跨日邊界問題
        // 如果現在是凌晨 00:30 之前，那 "今天" 的預約不可能遲到 30 分鐘
        // 所以將 cutoffTime 設為 00:00，確保只會抓到 "昨天以前" 的預約
        if (now.getHour() == 0 && now.getMinute() < 30) {
            cutoffTime = LocalTime.MIN; 
        } else {
            // 正常情況：截止時間 = 現在時間 - 30 分鐘
            cutoffTime = now.minusMinutes(30);
        }

        // 1. 查詢符合條件的預約單
        List<Appointment> expiredAppointments = appointmentRepository.findExpiredAppointments(
                AppConstants.APPOINTMENT_STATUS_CONFIRMED, 
                today, 
                cutoffTime
        );

        // 2. 批次更新狀態
        if (!expiredAppointments.isEmpty()) {
            logger.info("排程器發現 {} 筆過期且未報到的預約，正在更新狀態為『未到店』。", expiredAppointments.size());
            for (Appointment appointment : expiredAppointments) {
                appointment.setAppointmentStatus(AppConstants.APPOINTMENT_STATUS_NO_SHOW);
                
                logger.debug("標記預約 ID {} 為未到店。日期: {}, 時間: {}", 
                        appointment.getAppointmentId(), appointment.getAppointmentDate(), appointment.getStartTime());
            }
            appointmentRepository.saveAll(expiredAppointments);
        }
    }
}
