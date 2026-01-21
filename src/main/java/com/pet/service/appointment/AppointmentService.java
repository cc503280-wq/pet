package com.pet.service.appointment;

import com.pet.common.AppConstants;
import com.pet.dao.appointment.*;
import com.pet.dao.member.MemberRepository;
import com.pet.model.appointment.Appointment;
import com.pet.model.appointment.AppointmentDetailList;
import com.pet.model.appointment.AppointmentDetails;
import com.pet.model.appointment.AppointmentList;
import com.pet.model.appointment.AppointmentRequest;
import com.pet.model.appointment.DailySchedule;
import com.pet.model.appointment.ServiceItem;
import com.pet.model.member.Member;
import com.pet.util.TimeSlotUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.io.ByteArrayOutputStream;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AppointmentService: 預約系統的核心業務邏輯層
 * 負責處理：新增預約(含時段檢查)、取消預約、查詢預約、完成預約等
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {

    private static final int URGENT_CANCEL_HOURS_THRESHOLD = 6;

    private final DailyScheduleRepository dailyScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentListRepository appointmentListRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final AppointmentDetailListRepository appointmentDetailListRepository;
    private final LineNotificationService lineNotificationService;
    private final MailService mailService;
    private final MemberRepository memberRepository;

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

        checkUrgentCancellationAndNotify(appointment);

        Appointment saved = updateStatus(appointment, AppConstants.APPOINTMENT_STATUS_CANCELLED,
                appt -> log.info("預約單號：{} 取消預約", appt.getAppointmentId()));

        restoreGroomerSchedule(saved);

        return saved;
    }

    @Transactional
    public Appointment completeAppointment(Integer id) {
        return updateAppointmentStatus(id, AppConstants.APPOINTMENT_STATUS_COMPLETED, "預約單號：{} 服務完成");
    }

    @Transactional
    public Appointment checkInAppointment(Integer id) {
        return updateAppointmentStatus(id, AppConstants.APPOINTMENT_STATUS_IN_PROGRESS, "預約單號：{} 報到成功");
    }

    // ==================== 新增預約 ====================

    @Transactional
    public Appointment saveAppointment(AppointmentRequest request) {
        try {
            // 1. 資料準備與檢查
            LocalTime startTime = LocalTime.parse(request.getStartTime());
            LocalTime endTime = LocalTime.parse(request.getEndTime());
            DailySchedule schedule = getScheduleOrThrow(request.getGroomerId(), request.getAppointmentDate());
            List<ServiceItem> selectedServices = getServicesOrThrow(request.getServiceIds());

            // 2. 計算與邏輯驗證
            int totalDuration = calculateTotalDuration(selectedServices);
            validateGroomerAvailability(schedule, startTime, totalDuration);
            validatePetAvailability(request.getPetId(), request.getAppointmentDate(), startTime, endTime);

            // 3. 組裝與存檔
            Appointment appointment = createAppointmentEntity(request, selectedServices, startTime, endTime);
            Appointment savedAppt = appointmentRepository.save(appointment);
            log.info("預約單號: {} 建立成功, 總時長: {} 分鐘", savedAppt.getAppointmentId(), totalDuration);

            // 4. 鎖定時段
            // 修改美容師班表，把時段鎖起來 (0 -> 1)
            lockGroomerSchedule(schedule, startTime, totalDuration);

            // 5. 發送通知
            sendAppointmentConfirmationEmail(savedAppt, request.getMemberId());

            return savedAppt;

        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("樂觀鎖衝突: {}", e.getMessage());
            throw new RuntimeException("該時段剛被其他人預約，請重新選擇時段！");
        }
    }

    // ==================== 私有輔助方法 ====================

    private Appointment findAppointmentOrThrow(Integer id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的預約"));
    }

    private Appointment updateAppointmentStatus(Integer id, String newStatus, String logMessage) {
        Appointment appointment = findAppointmentOrThrow(id);
        return updateStatus(appointment, newStatus, appt -> log.info(logMessage, appt.getAppointmentId()));
    }

    private Appointment updateStatus(Appointment appointment, String newStatus, Consumer<Appointment> onSuccess) {
        appointment.setAppointmentStatus(newStatus);
        Appointment saved = appointmentRepository.save(appointment);
        if (saved != null) {
            onSuccess.accept(saved);
        }
        return saved;
    }

    private void checkUrgentCancellationAndNotify(Appointment appointment) {
        try {
            LocalDateTime appointmentDateTime = LocalDateTime.of(
                    appointment.getAppointmentDate(), appointment.getStartTime());
            LocalDateTime now = LocalDateTime.now();
            long hoursDifference = Duration.between(now, appointmentDateTime).toHours();

            log.info("檢查取消時間政策。預約時間: {}, 當前時間: {}, 差距小時: {}",
                    appointmentDateTime, now, hoursDifference);

            if (hoursDifference < URGENT_CANCEL_HOURS_THRESHOLD && hoursDifference >= -1) {
                String groomerName = (appointment.getGroomer() != null)
                        ? appointment.getGroomer().getGroomerName()
                        : "Unknown";
                log.info("偵測到臨時取消 (< {} 小時)。觸發 LINE 通知...", URGENT_CANCEL_HOURS_THRESHOLD);
                lineNotificationService.sendCancellationNotification(appointment, groomerName);
            } else {
                log.info("取消時間在 {} 小時之前。不觸發通知。", URGENT_CANCEL_HOURS_THRESHOLD);
            }
        } catch (Exception e) {
            log.error("執行 LINE 通知檢查失敗", e);
        }
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

    private void sendAppointmentConfirmationEmail(Appointment appointment, Integer memberId) {
        try {
            Member member = memberRepository.findById(memberId).orElse(null);
            if (member != null && member.getEmail() != null) {
                mailService.sendAppointmentSuccessEmail(member.getEmail(), member.getName(), appointment);
                log.info("預約成功郵件已觸發發送給 {}", member.getEmail());
            }
        } catch (Exception e) {
            log.error("發送預約通知信失敗，但預約已成功", e);
        }
    }

    // 找班表
    private DailySchedule getScheduleOrThrow(Integer groomerId, LocalDate date) {
        return dailyScheduleRepository.findByGroomerIdAndWorkDate(groomerId, date)
                .orElseThrow(() -> new RuntimeException("該美容師當日無排班"));
    }

    // 找服務項目
    private List<ServiceItem> getServicesOrThrow(List<Integer> serviceIds) {
        List<ServiceItem> services = serviceItemRepository.findAllById(serviceIds);
        if (services.isEmpty()) {
            throw new RuntimeException("未選擇有效的服務項目");
        }
        return services;
    }

    // 計算總時長
    private int calculateTotalDuration(List<ServiceItem> services) {
        return services.stream()
                .mapToInt(ServiceItem::getDurationMinutes)
                .sum();
    }

    // 查美容師是否有空 (利用 TimeSlotUtils)
    private void validateGroomerAvailability(DailySchedule schedule, LocalTime startTime, int totalDuration) {
        int startIndex = TimeSlotUtils.timeToStartIndex(startTime);
        int slotsNeeded = TimeSlotUtils.calculateSlotsNeeded(totalDuration);

        if (!TimeSlotUtils.isSegmentAvailable(schedule.getTimeSlots(), startIndex, slotsNeeded)) {
            throw new RuntimeException("該時段已被預約！");
        }
    }

    // 檢查寵物是否有重複預約
    private void validatePetAvailability(Integer petId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        int conflictCount = appointmentRepository.countPetActiveAppointments(petId, date, startTime, endTime);
        if (conflictCount > 0) {
            throw new RuntimeException("該寵物在此時段已有其他預約！");
        }
    }

    // 組裝實體
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
        appointment.setPayStatus(AppConstants.PAY_STATUS_UNPAID);

        // 處理服務明細
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

    // 鎖定班表並存檔
    private void lockGroomerSchedule(DailySchedule schedule, LocalTime startTime, int totalDuration) {
        int startIndex = TimeSlotUtils.timeToStartIndex(startTime);
        String lockedSlots = TimeSlotUtils.lockSlots(schedule.getTimeSlots(), startIndex, totalDuration);
        schedule.setTimeSlots(lockedSlots);
        dailyScheduleRepository.save(schedule);
        log.info("已將美容師: {} 工作日: {} 時段鎖定", schedule.getGroomerId(), schedule.getWorkDate());
    }

    // 生成 QR Code 圖片 (byte[])
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
