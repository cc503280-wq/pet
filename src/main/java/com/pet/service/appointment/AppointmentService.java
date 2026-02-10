package com.pet.service.appointment;

import com.pet.common.AppConstants;
import com.pet.dao.appointment.AppointmentRepository;
import com.pet.dao.appointment.AppointmentListRepository;
import com.pet.dao.appointment.DailyScheduleRepository;
import com.pet.dao.appointment.AppointmentDetailListRepository;
import com.pet.dao.appointment.ServiceItemRepository;
import com.pet.model.appointment.Appointment;
import com.pet.model.appointment.AppointmentDetailList;
import com.pet.model.appointment.AppointmentDetails;
import com.pet.model.appointment.AppointmentList;
import com.pet.model.appointment.AppointmentRequest;
import com.pet.model.appointment.DailySchedule;
import com.pet.model.appointment.ServiceItem;
import com.pet.util.TimeSlotUtils;
import com.pet.aspect.LogAction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AppointmentService: 預約系統的核心業務邏輯層
 * 負責處理：新增預約、取消預約、查詢預約、完成預約等
 * (通知與驗證邏輯已抽取至 Helper Services)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentListRepository appointmentListRepository;
    private final AppointmentDetailListRepository appointmentDetailListRepository;
    private final DailyScheduleRepository dailyScheduleRepository;
    private final ServiceItemRepository serviceItemRepository;

    private final AppointmentNotificationService notificationService;
    private final AppointmentValidationService validationService;

    // ==================== 查詢方法 ====================

    public List<AppointmentList> getAllAppointments() {
        return appointmentListRepository.findAll();
    }

    public AppointmentList getAppointmentListById(Integer id) {
        return appointmentListRepository.findById(id).orElse(null);
    }

    public List<AppointmentList> getAppointmentsByGroomerId(String groomerId) {
        return appointmentListRepository.findByGroomerId(groomerId);
    }

    public List<AppointmentList> getAppointmentsByMemberId(Integer memberId) {
        return appointmentListRepository.findByMemberId(String.valueOf(memberId));
    }

    public List<ServiceItem> findServicesByPetTypeAndPetSize(String petType, String petSize) {
        List<String> sizeCriteria = Arrays.asList(petSize, "不分體型");
        return serviceItemRepository.findByTargetPetTypeAndTargetPetSizeInAndIsActiveTrue(petType, sizeCriteria);
    }

    public List<AppointmentList> searchAppointments(String memberPhone, String status, String startDate,
            String endDate, String groomerId, String createdAtStart, String createdAtEnd) {
        return appointmentListRepository.complexSearch(memberPhone, status, startDate, endDate,
                groomerId, createdAtStart, createdAtEnd);
    }

    public List<AppointmentDetailList> getAllDetails() {
        return appointmentDetailListRepository.findAll();
    }

    public List<AppointmentDetailList> getDetailsByAppointmentId(Integer appointmentId) {
        return appointmentDetailListRepository.findByAppointmentId(appointmentId);
    }

    // ==================== 狀態變更方法 ====================

    @Transactional
    public Appointment cancelAppointment(Integer id) {
        Appointment appointment = findAppointmentOrThrow(id);

        // 發送通知 (含緊急取消檢查)
        notificationService.sendCancellation(appointment, 6); // 6 hours threshold

        Appointment saved = updateStatus(appointment, AppConstants.APPOINTMENT_STATUS_CANCELLED,
                appt -> log.info("預約單號：{} 取消預約", appt.getAppointmentId()));

        restoreGroomerSchedule(saved);
        
        return saved;
    }

    @Transactional
    public Appointment completeAppointment(Integer id) {
        Appointment appointment = findAppointmentOrThrow(id);
        
        Appointment saved = updateStatus(appointment, AppConstants.APPOINTMENT_STATUS_COMPLETED,
                appt -> log.info("預約單號：{} 服務完成", appt.getAppointmentId()));

        notificationService.sendCompletion(saved);
        
        return saved;
    }

    @Transactional
    public Appointment checkInAppointment(Integer id) {
        Appointment appointment = findAppointmentOrThrow(id);

        LocalDate today = LocalDate.now();
        LocalDate appointmentDate = appointment.getAppointmentDate();

        if (!appointmentDate.equals(today)) {
             // 報到日期檢查邏輯保留在此，或是也可以移到 ValidationService
             // 但這牽涉到 "今天" 的動態時間，且有特定錯誤訊息
             String errorMessage;
             if (appointmentDate.isBefore(today)) {
                 errorMessage = String.format("此預約日期為 %s，已經過期！今天是 %s。", appointmentDate, today);
             } else {
                 errorMessage = String.format("此預約日期為 %s，是未來的預約！今天是 %s。", appointmentDate, today);
             }
             throw new RuntimeException(errorMessage);
        }

        appointment.setPayStatus(true); // Mark as paid

        Appointment saved = updateStatus(appointment, AppConstants.APPOINTMENT_STATUS_CHECKED_IN,
                appt -> log.info("預約單號：{} 報到成功，付款狀態已更新", appt.getAppointmentId()));

        notificationService.broadcastUpdate(saved, AppConstants.EVENT_CHECKIN, "預約 #" + saved.getAppointmentId() + " 已報到成功！", null);
        
        return saved;
    }

    @Transactional
    public Appointment startAppointment(Integer id) {
        Appointment appointment = findAppointmentOrThrow(id);
        
        if (!AppConstants.APPOINTMENT_STATUS_CHECKED_IN.equals(appointment.getAppointmentStatus())) {
            throw new RuntimeException("預約狀態必須為「已報到」才能開始服務");
        }

        Appointment saved = updateStatus(appointment, AppConstants.APPOINTMENT_STATUS_IN_PROGRESS,
                appt -> log.info("預約單號：{} 開始服務", appt.getAppointmentId()));

        notificationService.broadcastUpdate(saved, AppConstants.EVENT_START, "預約 #" + saved.getAppointmentId() + " 開始美容囉！✂️", null);
        
        return saved;
    }

    // ==================== 新增預約 ====================
    
    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class }, 
            noRetryFor = { RuntimeException.class },
            maxAttempts = 25,  
            backoff = @Backoff(delay = 20, multiplier = 1.1, maxDelay = 300, random = true)
        )
    @Transactional
    @LogAction(type = LogAction.ActionType.BOOKING) // [AOP] 紀錄預約
    public Appointment saveAppointment(AppointmentRequest request) {
       
            // 1. 資料準備 (使用 ValidationService)
            LocalTime startTime = LocalTime.parse(request.getStartTime());
            LocalTime endTime = LocalTime.parse(request.getEndTime());
            DailySchedule schedule = validationService.getScheduleOrThrow(request.getGroomerId(), request.getAppointmentDate());
            List<ServiceItem> selectedServices = validationService.getServicesOrThrow(request.getServiceIds());

            // 2. 邏輯驗證 (使用 ValidationService)
            int totalDuration = validationService.calculateTotalDuration(selectedServices);
            validationService.validateGroomerAvailability(schedule, startTime, totalDuration);
            validationService.validatePetAvailability(request.getPetId(), request.getAppointmentDate(), startTime, endTime);

            // 3. 組裝與存檔
            Appointment appointment = createAppointmentEntity(request, selectedServices, startTime, endTime);
            Appointment savedAppt = appointmentRepository.save(appointment);
            log.info("預約單號: {} 建立成功, 總時長: {} 分鐘", savedAppt.getAppointmentId(), totalDuration);
            
            // 4. 鎖定時段 (先鎖定，確保成功後才發送通知)
            dailyScheduleRepository.flush();
            lockGroomerSchedule(schedule, startTime, totalDuration);

            // 5. 發送確認通知 (鎖定成功後才寄信，避免併發時兩人都收到確認信)
            notificationService.sendConfirmation(savedAppt, request.getMemberId());

            return savedAppt;

    }

    @Recover
    public Appointment recover(RuntimeException e, AppointmentRequest request) {
        log.warn("🛑 攔截到非併發錯誤 ({}): {}", e.getClass().getSimpleName(), e.getMessage());
        throw e;
    }
    
    @Recover
    public Appointment recover(ObjectOptimisticLockingFailureException e, AppointmentRequest request) {
        log.error("已重試 3 次，但仍發生樂觀鎖衝突。放棄預約。請求: {}", request);
        throw new RuntimeException("系統繁忙（多人搶訂中），請重新整理頁面後再試！");
    }

    @Recover
    public Appointment recover(Throwable e, AppointmentRequest request) {
        log.error("預約流程發生非預期錯誤 (Recover): class={}, msg={}", e.getClass().getName(), e.getMessage());
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        throw new RuntimeException(e.getMessage());
    }

    // ==================== 私有輔助方法 ====================

    private Appointment findAppointmentOrThrow(Integer id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的預約"));
    }

    private Appointment updateStatus(Appointment appointment, String newStatus, Consumer<Appointment> onSuccess) {
        appointment.setAppointmentStatus(newStatus);
        Appointment saved = appointmentRepository.save(appointment);
        if (saved != null) {
            onSuccess.accept(saved);
        }
        return saved;
    }

    private void restoreGroomerSchedule(Appointment appointment) {
        Integer groomerId = appointment.getGroomerId();
        LocalDate cancelDate = appointment.getAppointmentDate();
        LocalTime startTime = appointment.getStartTime();
        LocalTime endTime = appointment.getEndTime();

        long durationMinutes = Duration.between(startTime, endTime).toMinutes();
        int targetDuration = (int) durationMinutes;

        Optional<DailySchedule> scheduleOpt = dailyScheduleRepository
                .findByGroomerIdAndWorkDate(groomerId, cancelDate);

        if (scheduleOpt.isPresent()) {
            DailySchedule schedule = scheduleOpt.get();
            String currentSlots = schedule.getTimeSlots();
            int startIndex = TimeSlotUtils.timeToStartIndex(startTime);

            String unlockedSlots = TimeSlotUtils.unLockSlots(currentSlots, startIndex, targetDuration);
            schedule.setTimeSlots(unlockedSlots);

            dailyScheduleRepository.save(schedule);
            log.info("已將美容師: {} 工作日: {} 時段 {}~{} 解鎖", groomerId, cancelDate, startTime, endTime);
        } else {
            log.error("預約單號：{} 取消預約失敗: 找不到班表", appointment.getAppointmentId());
        }
    }

    private Appointment createAppointmentEntity(AppointmentRequest request, List<ServiceItem> services,
            LocalTime startTime, LocalTime endTime) {
        Appointment appointment = new Appointment();
        appointment.setPetId(request.getPetId());
        appointment.setGroomerId(request.getGroomerId());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setFinalPrice(request.getTotalPrice());
        appointment.setNotes(request.getNotes());
        appointment.setAppointmentStatus(AppConstants.APPOINTMENT_STATUS_CONFIRMED);
        appointment.setPayStatus(false); 

        for (ServiceItem svc : services) {
            AppointmentDetails detail = new AppointmentDetails();
            detail.setServiceId(svc.getServiceId());
            detail.setPrice(svc.getPrice());
            detail.setDurationMinutes(svc.getDurationMinutes());
            detail.setAppointment(appointment);
            appointment.getAppointmentDetails().add(detail);
        }
        return appointment;
    }

    private void lockGroomerSchedule(DailySchedule schedule, LocalTime startTime, int totalDuration) {
        int startIndex = TimeSlotUtils.timeToStartIndex(startTime);
        String lockedSlots = TimeSlotUtils.lockSlots(schedule.getTimeSlots(), startIndex, totalDuration);
        schedule.setTimeSlots(lockedSlots);
        dailyScheduleRepository.saveAndFlush(schedule);
        log.info("已將美容師: {} 工作日: {} 時段鎖定", schedule.getGroomerId(), schedule.getWorkDate());
    }

    public byte[] generateQRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("QR Code 生成失敗", e);
            throw new RuntimeException("無法生成 QR Code", e);
        }
    }
}
