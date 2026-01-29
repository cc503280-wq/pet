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
import com.pet.model.member.MemberPet;
import com.pet.util.TimeSlotUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.io.ByteArrayOutputStream;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
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
    private final MemberRepository memberRepository;
    //注入Line通知功能
    private final LineNotificationService lineNotificationService;
    //注入Mail通知功能
    private final MailService mailService;
    //注入SimpMessagingTemplate-WebSocket功能
    private final SimpMessagingTemplate messagingTemplate;
    //注入Twilio SMS通知功能
    private final TwilioSmsService twilioSmsService;

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
    	Appointment appointment = findAppointmentOrThrow(id);
    	
    	
    	//準備Websocket
    	Map<String, Object> message = new HashMap<>();
        message.put("message", "您的毛孩服務已完成，可以來接牠囉！🐾");
        message.put("status", AppConstants.APPOINTMENT_STATUS_COMPLETED);  // "已完成"
        message.put("appointmentId", id);
        
        //取得會員ID
        Integer memberId = appointment.getMemberPet().getMember().getMemberId();
        
       // 發送 WebSocket 通知給會員
        messagingTemplate.convertAndSend("/topic/member/" + memberId + "/appointments", (Object) message);
    	
        //FIXME:記得改回來
        //sendCompletionNotifications(appointment);
    	
        return updateStatus(appointment, AppConstants.APPOINTMENT_STATUS_COMPLETED,
                appt -> log.info("預約單號：{} 服務完成", appt.getAppointmentId()));
    }

    @Transactional
    public Appointment checkInAppointment(Integer id) {
        Appointment appointment = findAppointmentOrThrow(id);

        LocalDate today = LocalDate.now();
        LocalDate appointmentDate = appointment.getAppointmentDate();
       
        if (!appointmentDate.equals(today)) {
            String errorMessage;
            if (appointmentDate.isBefore(today)) {
                errorMessage = String.format(
                    "此預約日期為 %s，已經過期！今天是 %s。如需報到，請聯繫管理員。",
                    appointmentDate, today
                );
            } else {
                errorMessage = String.format(
                    "此預約日期為 %s，是未來的預約！今天是 %s。請於預約當天再進行報到。",
                    appointmentDate, today
                );
            }
            log.warn("報到失敗 - 日期不符。預約單號: {}, 預約日期: {}, 今天: {}", id, appointmentDate, today);
            throw new RuntimeException(errorMessage);
        }

        // 先準備一個要傳送的內容 (例如用 Map)
        Map<String, Object> message = new HashMap<>(); 
        message.put("message", "您的毛孩已開始服務！");
        message.put("status", AppConstants.APPOINTMENT_STATUS_IN_PROGRESS);
        message.put("appointmentId", id);

        Integer memberId = appointment.getMemberPet().getMember().getMemberId();

        // 使用 messagingTemplate 傳送 WebSocket 消息
       messagingTemplate.convertAndSend("/topic/member/" + memberId + "/appointments", (Object) message);
        
        return updateStatus(appointment, AppConstants.APPOINTMENT_STATUS_IN_PROGRESS,
                appt -> log.info("預約單號：{} 報到成功", appt.getAppointmentId()));
    }

    // ==================== 新增預約 ====================
    
    @Retryable(
    		retryFor = { ObjectOptimisticLockingFailureException.class }, 
    		noRetryFor = { RuntimeException.class },
    	    maxAttempts = 25,  
    	    backoff = @Backoff(
    	        delay = 20,      // 初始等待縮短一點
    	        multiplier = 1.1, // 每次增加 1.5 倍
    	        maxDelay = 300,  // 最久不等超過 1 秒
    	        random = true     // 👉 關鍵！開啟隨機，讓大家不要「一起醒來」
    	    )
    	)
    @Transactional
    public Appointment saveAppointment(AppointmentRequest request) {
       
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
            
            dailyScheduleRepository.flush();

            // 4. 鎖定時段
            // 修改美容師班表，把時段鎖起來 (0 -> 1)
            lockGroomerSchedule(schedule, startTime, totalDuration);

            // 5. 發送通知
            sendAppointmentConfirmationEmail(savedAppt, request.getMemberId());

            return savedAppt;

    }
    
    @Recover
    public Appointment recover(RuntimeException e, AppointmentRequest request) {
        log.warn("🛑 攔截到非併發錯誤 ({}): {}", e.getClass().getSimpleName(), e.getMessage());
        
        // 什麼都不做，直接把原本的錯誤 (例如: "該時段已被預約") 往外丟
        // 這樣 Controller 就會收到正確的錯誤訊息，而不是 ExhaustedRetryException
        throw e;
    }
    
    
    @Recover
    public Appointment recover(ObjectOptimisticLockingFailureException e, AppointmentRequest request) {
        log.error("已重試 3 次，但仍發生樂觀鎖衝突。放棄預約。請求: {}", request);
        // 這裡拋出的異常會傳給前端 Controller
        throw new RuntimeException("系統繁忙（多人搶訂中），請重新整理頁面後再試！");
    }

    // ==================== 私有輔助方法 ====================

    private Appointment findAppointmentOrThrow(Integer id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的預約"));
    }

    // private Appointment updateAppointmentStatus(Integer id, String newStatus, String logMessage) {
    //     Appointment appointment = findAppointmentOrThrow(id);
    //     return updateStatus(appointment, newStatus, appt -> log.info(logMessage, appt.getAppointmentId()));
    // }

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
                //TODO:等等需要再打開通知，目前先取消
                //lineNotificationService.sendCancellationNotification(appointment, groomerName);
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

    
    /**
     * 發送服務完成通知 (使用 Twilio SMS 簡訊)
     */
    private void sendCompletionNotifications(Appointment appointment) {
        try {
            // 1. 透過 JPA 關聯取得會員資料 (Appointment -> MemberPet -> Member)
            MemberPet pet = appointment.getMemberPet();
            if (pet == null) {
                log.warn("找不到預約的寵物資料，無法發送通知");
                return;
            }
            
            Member member = pet.getMember();
            if (member == null) {
                log.warn("找不到會員資料，無法發送通知");
                return;
            }
            
            // 2. 發送 SMS 簡訊通知 (如果有手機號碼)
            if (member.getPhone() != null && !member.getPhone().isEmpty()) {
                twilioSmsService.sendServiceCompletedSms(member.getPhone(), appointment);
                log.info("已觸發 SMS 通知給會員: {}", member.getPhone());
            } else {
                log.warn("會員沒有手機號碼，無法發送 SMS");
            }
        } catch (Exception e) {
            log.error("發送服務完成通知失敗，但服務已完成", e);
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
        //如果衝突，直接拋出Exception，這樣就不會沒有預約到的人也收到確認郵件
        dailyScheduleRepository.saveAndFlush(schedule);
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
