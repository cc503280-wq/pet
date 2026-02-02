package com.pet.controller.member;

import com.pet.dao.member.MemberActionLogRepository;
import com.pet.model.member.MemberActionLog;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/logs")
public class MemberActionLogController {

    @Autowired
    private MemberActionLogRepository logRepository;

    @GetMapping("/recent")
    public List<Map<String, Object>> getLogs(
            @RequestParam(required = false) Integer memberId,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "10") int limit) {
        // 建立查詢條件
        org.springframework.data.jpa.domain.Specification<MemberActionLog> spec = createSpecification(memberId,
                actionType, startDate, endDate);

        // 執行查詢 (分頁 + 排序)
        Page<MemberActionLog> page = logRepository.findAll(spec,
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "actionTime")));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return page.getContent().stream().map(log -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("memberId", log.getMemberId());
            map.put("actionType", log.getActionType());
            map.put("targetId", formatTargetId(log.getActionType(), log.getTargetId()));
            map.put("detail", log.getDetail());
            map.put("clientIp", log.getClientIp());
            map.put("time", log.getActionTime().format(formatter));
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/export")
    public void exportLogs(
            HttpServletResponse response,
            @RequestParam(required = false) Integer memberId,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String filename = "member_logs_" + LocalDate.now() + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        try (java.io.PrintWriter writer = response.getWriter()) {
            writer.write('\ufeff');
            writer.println("時間,會員ID,動作類型,目標ID,內容,IP來源");

            // 建立查詢條件
            org.springframework.data.jpa.domain.Specification<MemberActionLog> spec = createSpecification(memberId,
                    actionType, startDate, endDate);

            // 執行查詢 (全部 + 排序)
            List<MemberActionLog> logs = logRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "actionTime"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (MemberActionLog log : logs) {
                String detail = (log.getDetail() == null) ? "" : log.getDetail().replace(",", "，").replace("\n", " ");
                String formattedTargetId = formatTargetId(log.getActionType(), log.getTargetId());

                writer.println(String.join(",",
                        log.getActionTime().format(formatter),
                        String.valueOf(log.getMemberId()),
                        log.getActionType(),
                        formattedTargetId,
                        detail,
                        log.getClientIp()));
            }
        }
    }

    // 輔助方法：根據動作類型為 ID 加上敘述
    private String formatTargetId(String actionType, String targetId) {
        if (targetId == null || targetId.isEmpty() || "null".equals(targetId)) {
            return "-";
        }
        if (actionType == null) {
            return targetId;
        }

        switch (actionType) {
            case "CART":
            case "FAVORITE":
            case "ADD_TO_CART": // 兼容可能得舊字串
            case "VIEW_PRODUCT":
                return "商品ID: " + targetId;
            case "CREATE_ORDER":
                return "訂單ID: " + targetId;
            case "BOOKING":
                return "寵物ID: " + targetId; // 根據 Aspect 邏輯，目前存的是 PetID
            default:
                return targetId;
        }
    }

    // 私有方法：建立動態查詢條件 (Dynamic Specification)
    // 這裡使用了 Spring Data JPA 的 Specification 介面與 JPA Criteria API
    private Specification<MemberActionLog> createSpecification(
            Integer memberId, String actionType, String startDate, String endDate) {

        // Specification 是一個 Functional Interface，這裡使用 Lambda 表達式實作
        // root: 代表查詢的實體 (MemberActionLog)，用來存取欄位 (例如 root.get("memberId"))
        // query: 用來控制查詢的結構 (例如 select, where, orderBy)，此處主要用於回傳 Predicate
        // cb (CriteriaBuilder): 用來建構查詢條件的工廠 (例如 equal, between, like, and, or)
        return (root, query, cb) -> {

            // 建立一個 List 來暫存所有的查詢條件 (Predicates)
            // Predicate 在 JPA 中代表一個 "WHERE" 子句的條件
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // 1. 篩選會員 ID (Exact Match)
            // SQL: WHERE member_id = ?
            if (memberId != null) {
                // cb.equal(欄位, 值): 產生 "欄位 = 值" 的條件
                predicates.add(cb.equal(root.get("memberId"), memberId));
            }

            // 2. 篩選動作類型 (Exact Match)
            // SQL: AND action_type = ?
            if (actionType != null && !actionType.isEmpty()) {
                predicates.add(cb.equal(root.get("actionType"), actionType));
            }

            // 3. 篩選日期範圍 (Range Query)
            // SQL: AND action_time BETWEEN ? AND ?
            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                // 將字串日期 (yyyy-MM-dd) 轉換為 LocalDateTime
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);

                // 起始時間設為當天的 00:00:00
                LocalDateTime startDateTime = start.atStartOfDay();
                // 結束時間設為當天的 23:59:59.999999999
                LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

                // cb.between(欄位, 起始值, 結束值): 產生 "欄位 BETWEEN 起始 AND 結束" 的條件
                predicates.add(cb.between(root.get("actionTime"), startDateTime, endDateTime));
            }

            // cb.and(): 將所有條件用 "AND" 連接起來
            // predicates.toArray(...): 將 List 轉為 Array 傳入
            // 最終產生類似: WHERE member_id = ? AND action_type = ? AND action_time BETWEEN ? AND
            // ?
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
