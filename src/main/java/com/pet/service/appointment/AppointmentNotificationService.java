package com.pet.service.appointment;

import com.pet.common.AppConstants;
import com.pet.dao.member.MemberRepository;
import com.pet.model.appointment.Appointment;
import com.pet.model.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentNotificationService {

    private final LineNotificationService lineNotificationService;
    private final MailService mailService;
    private final SimpMessagingTemplate messagingTemplate;
    private final TwilioSmsService twilioSmsService;
    private final MemberRepository memberRepository;

    /**
     * 發送預約成功通知 (Email & WebSocket)
     */
    public void sendConfirmation(Appointment appointment, Integer memberId) {
        // 1. WebSocket Broadcast
        broadcastUpdate(appointment, AppConstants.EVENT_NEW_APPOINTMENT, "新預約成立 #" + appointment.getAppointmentId(), memberId);
        
        // 2. Email Notification
        try {
            Member member = memberRepository.findById(memberId).orElse(null);
            if (member != null && member.getEmail() != null) {
                mailService.sendAppointmentSuccessEmail(member.getEmail(), member.getName(), appointment);
                log.info("預約成功郵件已觸發發送給 {}", member.getEmail());
            }
        } catch (Exception e) {
            log.error("發送預約通知信失敗", e);
        }
    }

    /**
     * 發送服務完成通知 (SMS & WebSocket)
     */
    public void sendCompletion(Appointment appointment) {
        // 1. WebSocket Broadcast
        broadcastUpdate(appointment, AppConstants.EVENT_COMPLETED, "預約單號 #" + appointment.getAppointmentId() + " 已完成服務 🛁", null);

        // 2. SMS Notification
        try {
            // 透過 JPA 關聯取得會員資料
            if (appointment.getMemberPet() != null) {
                Member member = appointment.getMemberPet().getMember();
                if (member != null && member.getPhone() != null && !member.getPhone().isEmpty()) {
                    twilioSmsService.sendServiceCompletedSms(member.getPhone(), appointment);
                    log.info("已觸發 SMS 通知給會員: {}", member.getPhone());
                } else {
                    log.warn("會員沒有手機號碼或找不到會員，無法發送 SMS");
                }
            }
        } catch (Exception e) {
            log.error("發送服務完成通知失敗", e);
        }
    }

    /**
     * 發送預約取消通知 (WebSocket & 檢查是否需要緊急 Line 通知)
     */
    public void sendCancellation(Appointment appointment, Integer undoHoursThreshold) {
        // 1. WebSocket Broadcast
        broadcastUpdate(appointment, AppConstants.EVENT_CANCELLED, "預約單號 #" + appointment.getAppointmentId() + " 已取消", null);

        // 2. Urgent Cancellation Line Notification
        checkUrgentCancellationAndNotify(appointment, undoHoursThreshold);
    }
    
    /**
     * 檢查是否為緊急取消 (通常小於 6 小時)，如果是則發送 Line 通知給美容師/管理員
     */
    private void checkUrgentCancellationAndNotify(Appointment appointment, int thresholdHours) {
        try {
            LocalDateTime appointmentDateTime = LocalDateTime.of(
                    appointment.getAppointmentDate(), appointment.getStartTime());
            LocalDateTime now = LocalDateTime.now();
            long hoursDifference = java.time.Duration.between(now, appointmentDateTime).toHours();

            log.info("檢查取消時間政策。預約時間: {}, 當前時間: {}, 差距小時: {}",
                    appointmentDateTime, now, hoursDifference);

            if (hoursDifference < thresholdHours && hoursDifference >= -1) {
                String groomerName = (appointment.getGroomer() != null)
                        ? appointment.getGroomer().getGroomerName()
                        : "Unknown";
                log.info("偵測到臨時取消 (< {} 小時)。觸發 LINE 通知...", thresholdHours);
                
                lineNotificationService.sendCancellationNotification(appointment, groomerName);
            } else {
                log.info("取消時間在 {} 小時之前。不觸發通知。", thresholdHours);
            }
        } catch (Exception e) {
            log.error("執行 LINE 通知檢查失敗", e);
        }
    }

    /**
     * 統一廣播預約狀態更新給所有相關人員 (Admin, Member, Groomer)
     * @param specificMemberId 指定通知的會員 ID (若為 null 則嘗試從 Appointment 實體取得)
     */
    public void broadcastUpdate(Appointment appointment, String type, String messageText, Integer specificMemberId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", type);
            payload.put("appointmentId", appointment.getAppointmentId());
            payload.put("status", appointment.getAppointmentStatus());
            payload.put("payStatus", appointment.getPayStatus());
            payload.put("message", messageText);
            payload.put("timestamp", LocalDateTime.now().toString());

            // 1. Notify Admin
            messagingTemplate.convertAndSend(AppConstants.TOPIC_ADMIN_APPOINTMENTS, (Object) payload);

            // 2. Notify Member
            Integer targetMemberId = specificMemberId;
            if (targetMemberId == null && appointment.getMemberPet() != null && appointment.getMemberPet().getMember() != null) {
                targetMemberId = appointment.getMemberPet().getMember().getMemberId();
            }

            if (targetMemberId != null) {
                String memberTopic = String.format(AppConstants.TOPIC_MEMBER_PREFIX, targetMemberId);
                // The constant has %d, so we need String.format or just replace manually if format is simple.
                // Spring Stomp doesn't support regex topics easily, strict path.
                // AppConstants definition: "/topic/member/%d/appointments"
                messagingTemplate.convertAndSend(memberTopic, (Object) payload);
                log.info("WebSocket Sent to Member ID: {}", targetMemberId);
            } else {
                log.warn("WebSocket Skip Member: ID is null");
            }

            // 3. Notify Groomer (if assigned)
            if (appointment.getGroomerId() != null) {
                 String groomerTopic = String.format(AppConstants.TOPIC_GROOMER_PREFIX, appointment.getGroomerId());
                messagingTemplate.convertAndSend(groomerTopic, (Object) payload);
            }

            log.info("WebSocket Broadcast [{}]: Appointment ID {}", type, appointment.getAppointmentId());

        } catch (Exception e) {
            log.error("WebSocket 廣播失敗", e);
        }
    }
}
