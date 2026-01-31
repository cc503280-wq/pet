package com.pet.service.appointment;


import com.pet.dao.appointment.AppointmentRepository;
import com.pet.dao.appointment.DailyScheduleRepository;
import com.pet.dao.appointment.ServiceItemRepository;
import com.pet.model.appointment.DailySchedule;
import com.pet.model.appointment.ServiceItem;
import com.pet.util.TimeSlotUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentValidationService {

    private final DailyScheduleRepository dailyScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final ServiceItemRepository serviceItemRepository;

    /**
     * 驗證美容師是否有空 (利用 TimeSlotUtils)
     */
    public void validateGroomerAvailability(DailySchedule schedule, LocalTime startTime, int totalDuration) {
        int startIndex = TimeSlotUtils.timeToStartIndex(startTime);
        int slotsNeeded = TimeSlotUtils.calculateSlotsNeeded(totalDuration);

        if (!TimeSlotUtils.isSegmentAvailable(schedule.getTimeSlots(), startIndex, slotsNeeded)) {
            throw new RuntimeException("該時段已被預約！");
        }
    }

    /**
     * 檢查寵物是否有重複預約
     */
    public void validatePetAvailability(Integer petId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        int conflictCount = appointmentRepository.countPetActiveAppointments(petId, date, startTime, endTime);
        if (conflictCount > 0) {
            throw new RuntimeException("該寵物在此時段已有其他預約！");
        }
    }
    
    /**
     * 取得班表，若無則拋出異常
     */
    public DailySchedule getScheduleOrThrow(Integer groomerId, LocalDate date) {
        return dailyScheduleRepository.findByGroomerIdAndWorkDate(groomerId, date)
                .orElseThrow(() -> new RuntimeException("該美容師當日無排班"));
    }

    /**
     * 取得服務項目，若無則拋出異常
     */
    public List<ServiceItem> getServicesOrThrow(List<Integer> serviceIds) {
        List<ServiceItem> services = serviceItemRepository.findAllById(serviceIds);
        if (services.isEmpty()) {
            throw new RuntimeException("未選擇有效的服務項目");
        }
        return services;
    }

    /**
     * 計算服務總時長
     */
    public int calculateTotalDuration(List<ServiceItem> services) {
        return services.stream()
                .mapToInt(ServiceItem::getDurationMinutes)
                .sum();
    }
}
