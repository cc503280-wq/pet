package com.pet.scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pet.common.AppConstants;
import com.pet.dao.appointment.AppointmentRepository;
import com.pet.dao.appointment.DailyScheduleRepository;
import com.pet.model.appointment.Appointment;
import com.pet.model.appointment.DailySchedule;
import com.pet.util.TimeSlotUtils;

/**
 * AppointmentScheduler: 預約相關的定時任務排程器
 * 負責：自動掃描並更新過期預約的狀態 (如：逾期未到)。
 */
@Component
public class AppointmentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentScheduler.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DailyScheduleRepository dailyScheduleRepository;

    /**
     * 自動更新過期預約為「未到店 (No Show)」
     * 頻率：每 10 分鐘執行一次 
     * 條件：狀態為 "預約確認" 且 "當前時間已超過預約開始時間 30 分鐘"。
     * 新增：標記未到店後，自動釋放美容師班表時段，讓後續時間可以被其他會員預約。
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

        // 2. 批次更新狀態 & 釋放時段
        if (!expiredAppointments.isEmpty()) {
            logger.info("排程器發現 {} 筆過期且未報到的預約，正在更新狀態為『未到店』並釋放時段。", expiredAppointments.size());
            for (Appointment appointment : expiredAppointments) {
                appointment.setAppointmentStatus(AppConstants.APPOINTMENT_STATUS_NO_SHOW);
                
                // 釋放美容師班表時段，讓後面的時間可以被預約
                restoreGroomerSchedule(appointment);
                
                logger.debug("標記預約 ID {} 為未到店並釋放時段。日期: {}, 時間: {}", 
                        appointment.getAppointmentId(), appointment.getAppointmentDate(), appointment.getStartTime());
            }
            appointmentRepository.saveAll(expiredAppointments);
        }
    }

    /**
     * 釋放美容師班表時段
     * 當預約被標記為「未到店」時，將該預約佔用的時段解鎖，讓其他會員可以預約。
     */
    private void restoreGroomerSchedule(Appointment appointment) {
        Integer groomerId = appointment.getGroomerId();
        LocalDate appointmentDate = appointment.getAppointmentDate();
        LocalTime startTime = appointment.getStartTime();
        LocalTime endTime = appointment.getEndTime();

        long durationMinutes = Duration.between(startTime, endTime).toMinutes();
        int targetDuration = (int) durationMinutes;

        Optional<DailySchedule> scheduleOpt = dailyScheduleRepository
                .findByGroomerIdAndWorkDate(groomerId, appointmentDate);

        if (scheduleOpt.isPresent()) {
            DailySchedule schedule = scheduleOpt.get();
            String currentSlots = schedule.getTimeSlots();
            int startIndex = TimeSlotUtils.timeToStartIndex(startTime);

            String unlockedSlots = TimeSlotUtils.unLockSlots(currentSlots, startIndex, targetDuration);
            schedule.setTimeSlots(unlockedSlots);

            dailyScheduleRepository.save(schedule);
            logger.info("已將美容師: {} 工作日: {} 時段 {}~{} 解鎖（未到店釋放）", groomerId, appointmentDate, startTime, endTime);
        } else {
            logger.warn("預約 ID {} 未到店，但找不到美容師 {} 在 {} 的班表，無法釋放時段",
                    appointment.getAppointmentId(), groomerId, appointmentDate);
        }
    }
}
