package com.pet.controller.appointment;


import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.pet.dto.common.ApiResponse;
import com.pet.model.appointment.Appointment;
import com.pet.model.appointment.AppointmentDetailList;
import com.pet.model.appointment.AppointmentList;
import com.pet.model.appointment.AppointmentRequest;
import com.pet.model.appointment.ServiceItem;
import com.pet.service.appointment.AppointmentService;
import com.pet.util.LoginUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.pet.annotation.LogAction;

/**
 * AppointmentController: 負責處理所有與「預約」相關的 HTTP 請求
 * 包含：前台預約、後台管理列表、搜尋、狀態變更(取消/完成/報到)
 */
@Slf4j
@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    

    // ==================== 查詢 API ====================

    @GetMapping
    public ResponseEntity<List<AppointmentList>> getAllAppointments() {
        List<AppointmentList> list = appointmentService.getAllAppointments();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentList>> getMyAppointments(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<AppointmentList> list = appointmentService.getAppointmentsByMemberId(userId);
        return ResponseEntity.ok(list);
}

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentList> getAppointmentById(@PathVariable Integer id) {
        AppointmentList appointment = appointmentService.getAppointmentListById(id);
        if (appointment != null) {
            return ResponseEntity.ok(appointment);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 生成預約 QR Code (Appointment ID)
     * URL: /appointments/{id}/qrcode
     */
    @GetMapping(value = "/{id}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getAppointmentQRCode(@PathVariable Integer id) {
        // 確認預約存在
        AppointmentList appointment = appointmentService.getAppointmentListById(id);
        if (appointment == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // QR Code 內容：這裡簡單使用 AppointmentId，前端或店員端掃描後可查詢預約
            String qrContent = String.valueOf(id);

            // 生成 250x250 的 QR Code
            byte[] qrImage = appointmentService.generateQRCode(qrContent, 250, 250);

            return ResponseEntity.ok().body(qrImage);
        } catch (Exception e) {
            log.error("生成 QR Code 失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 會員取得自己的預約列表
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<AppointmentList>> getAppointmentsByMemberId(@PathVariable Integer memberId) {
        List<AppointmentList> list = appointmentService.getAppointmentsByMemberId(memberId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AppointmentList>> searchAppointments(
            @RequestParam(required = false) String memberPhone,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String groomerId,
            @RequestParam(required = false) String createdAtStart,
            @RequestParam(required = false) String createdAtEnd) {
        List<AppointmentList> list = appointmentService.searchAppointments(
                memberPhone, status, startDate, endDate, groomerId, createdAtStart, createdAtEnd);
        return ResponseEntity.ok(list);
    }

    /**
     * 美容師取得自己的任務列表
     */
    @GetMapping("/groomer/{groomerId}")
    public ResponseEntity<List<AppointmentList>> getAppointmentsByGroomerId(@PathVariable String groomerId) {
        List<AppointmentList> list = appointmentService.getAppointmentsByGroomerId(groomerId);
        return ResponseEntity.ok(list);
    }

    /**
     * 取得可用的服務項目 (依寵物類型和體型篩選)
     */
    @GetMapping("/services")
    public List<ServiceItem> getServices(@RequestParam String petType, @RequestParam String petSize) {
        return appointmentService.findServicesByPetTypeAndPetSize(petType, petSize);
    }

    /**
     * 取得所有預約明細
     */
    @GetMapping("/details")
    public List<AppointmentDetailList> getAllDetails() {
        return appointmentService.getAllDetails();
    }

    /**
     * 依預約 ID 取得該筆預約的明細
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<List<AppointmentDetailList>> getDetailsByAppointmentId(@PathVariable Integer id) {
        List<AppointmentDetailList> details = appointmentService.getDetailsByAppointmentId(id);
        return ResponseEntity.ok(details);
    }

    // ==================== 狀態變更 API ====================

    @PostMapping("/insertInto")
    public ResponseEntity<ApiResponse> insertAppointment(@Valid @RequestBody AppointmentRequest request) {
        try {
            Appointment appt = appointmentService.saveAppointment(request);
            return ResponseEntity.ok(ApiResponse.success("預約建立成功", appt.getAppointmentId()));
        } catch (Exception e) {
            log.error("新增預約失敗", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("預約失敗：" + e.getMessage()));
        }
    }

    @PatchMapping("/cancel/{appointmentId}")
    public ResponseEntity<ApiResponse> cancelAppointment(@PathVariable Integer appointmentId) {
        log.info("API: 取消預約 ID {}", appointmentId);
        appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.success("取消成功"));
    }
    
    @LogAction(actionType = "COMPLETE", description = "完成美容服務")
    @PatchMapping("/complete/{id}")
    public ResponseEntity<ApiResponse> completeAppointment(@PathVariable Integer id) {
        // 🛡️ 安全性改進：不再從前端接收 groomerId（改從 Session 取得）
        Appointment appointment = appointmentService.completeAppointment(id);
        log.info("預約 {} 完成", id);
        // ⚡ 效能優化：回傳 Appointment 物件供 AOP 使用
        return ResponseEntity.ok(ApiResponse.success("預約已完成", appointment));
    }

    @LogAction(actionType = "CHECK_IN", description = "會員到店報到")
    @PatchMapping("/check-in/{id}")
    public ResponseEntity<ApiResponse> checkInAppointment(@PathVariable Integer id) {
        // 🛡️ 安全性改進：不再從前端接收 groomerId（改從 Session 取得）
        Appointment appointment = appointmentService.checkInAppointment(id);
        log.info("預約 {} 報到", id);
        // ⚡ 效能優化：回傳 Appointment 物件供 AOP 使用
        return ResponseEntity.ok(ApiResponse.success("報到成功", appointment));
    }

}