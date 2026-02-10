package com.pet.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pet.dao.audit.GroomerActionLogRepository;
import com.pet.model.audit.GroomerActionLog;
import lombok.RequiredArgsConstructor;

/**
 * 審計日誌查詢 Controller
 * 提供後台管理員查詢操作日誌和效能監控數據的 API
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final GroomerActionLogRepository actionLogRepository;

    // ==================== 操作日誌查詢 ====================

    /**
     * 取得所有操作日誌
     */
    @GetMapping("/actions")
    public ResponseEntity<List<GroomerActionLog>> getAllActionLogs() {
        List<GroomerActionLog> logs = actionLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }

    /**
     * 依美容師 ID 查詢操作日誌
     */
    @GetMapping("/actions/groomer/{groomerId}")
    public ResponseEntity<List<GroomerActionLog>> getActionsByGroomer(
            @PathVariable Integer groomerId) {
        List<GroomerActionLog> logs = actionLogRepository.findByGroomerIdOrderByTimestampDesc(groomerId);
        return ResponseEntity.ok(logs);
    }

    /**
     * 依操作類型查詢
     */
    @GetMapping("/actions/type/{actionType}")
    public ResponseEntity<List<GroomerActionLog>> getActionsByType(
            @PathVariable String actionType) {
        List<GroomerActionLog> logs = actionLogRepository.findByActionType(actionType);
        return ResponseEntity.ok(logs);
    }

    /**
     * 依時間範圍查詢操作日誌
     */
    @GetMapping("/actions/range")
    public ResponseEntity<List<GroomerActionLog>> getActionsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<GroomerActionLog> logs = actionLogRepository.findByTimestampBetween(start, end);
        return ResponseEntity.ok(logs);
    }

}