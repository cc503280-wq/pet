package com.pet.controller.appointment;


import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.pet.model.appointment.Groomer;
import com.pet.model.appointment.LeaveRecord;
import com.pet.model.appointment.LeaveRecoredGroomerView;
import com.pet.service.appointment.GroomerService;
import lombok.extern.slf4j.Slf4j;


/**
 * GroomerController: 處理「美容師」相關 API
 * 包含：登入/登出、資料 CRUD、搜尋、以及「請假單」的管理
 */
@RestController 
@RequestMapping("/groomers") // 基礎路徑 /groomers
@Slf4j
public class GroomerController {

   	@Autowired
	private GroomerService groomerService;

    
	@GetMapping 
	public List<Groomer> getAllGroomer() {
		return groomerService.getAllGroomer();
	}

	
    // 複合條件搜尋美容師
	@GetMapping("/search") // GET /groomers/search
    public ResponseEntity<List<Groomer>> searchGroomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer isActive // 1:在職, 0:離職, null:全部
    ) {
        List<Groomer> result = groomerService.searchGroomers(name, startDate, endDate, isActive);
        return ResponseEntity.ok(result);
    }
	
	
    // 新增美容師 (含檔案上傳)
	@PostMapping("/insert") // POST /groomers/insert
	public ResponseEntity<?> insertGroomer(
	        @RequestParam("groomerName") String groomerName,
	        @RequestParam("phone") String phone,
	        @RequestParam("email") String email,
	        @RequestParam("password") String password,
	        @RequestParam("hiredate") LocalDate hiredate,
	        @RequestParam(value = "file", required = false) MultipartFile file) {
	    try {
	        Groomer groomer = new Groomer();
	        groomer.setGroomerName(groomerName);
	        groomer.setPhone(phone);
	        groomer.setEmail(email);
	        groomer.setPassword(password);
	        groomer.setHiredate(hiredate);       

	        groomerService.saveGroomerInfo(groomer,file);
	        return ResponseEntity.ok().body("美容師新增成功");
	    } catch (Exception e) {
	        log.error("新增失敗", e);
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}
	
	@GetMapping("/{id}")
    public ResponseEntity<?> getGroomerById(@PathVariable Integer id) {
        try {
            Groomer groomer = groomerService.getGroomerById(id);
            return ResponseEntity.ok(groomer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("查詢失敗：" + e.getMessage());
        }
    }
	
    // 更新美容師資料
	@PostMapping("/update/{id}") 
    public ResponseEntity<?> updateGroomerInfo(
            @PathVariable Integer id, 
            @RequestParam("groomerName") String groomerName,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("hiredate") LocalDate hiredate,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
          
            Groomer groomer = new Groomer();
            groomer.setGroomerName(groomerName);
            groomer.setPhone(phone);
            groomer.setEmail(email);
            groomer.setHiredate(hiredate);
            
            // 如果有傳入密碼，則設定密碼（Service 層會加密）
            if (password != null && !password.isEmpty()) {
                groomer.setPassword(password);
            }
            
            groomerService.updateGroomerInfo(id, groomer, file);
            
            return ResponseEntity.ok().body("{\"message\": \"美容師資料更新成功\"}");
        } catch (Exception e) {
            log.error("更新失敗", e);
            return ResponseEntity.badRequest().body("更新失敗：" + e.getMessage());
        }
    }
	
    // 更新美容師狀態 (在職/離職)
	@PatchMapping("/{id}/status")
    public ResponseEntity<?> updateGroomerStatus(
            @PathVariable Integer id,
            @RequestParam Boolean isActive) {
        try {
            // Service 會檢查是否有未來預約，避免錯誤停權
            groomerService.updateGroomerStatus(id, isActive);
            return ResponseEntity.ok().body("{\"message\": \"狀態更新成功\"}");
        } catch (Exception e) {
            log.error("狀態更新失敗", e);
            return ResponseEntity.badRequest().body("更新失敗：" + e.getMessage());
        }
    }
	
	//============ 請假單管理 (Leave Records) =======//
	
    // 取得所有請假紀錄 (View)
	@GetMapping(path = "/leave-records")
	public List<LeaveRecoredGroomerView> getAllLeaveRecords() {
		return groomerService.getAllLeaveRecords();
	}
	
    // 批次新增請假單 (前端現在呼叫這個)
	@PostMapping("/leave-records/batch")
    public ResponseEntity<?> createLeaveRecordBatch(@RequestBody com.pet.dto.appointment.LeaveRequestDto request) {
        try {
            groomerService.createLeaveRecordsBatch(request);
            return ResponseEntity.ok().body("{\"message\": \"請假申請已提交\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("請假申請失敗：" + e.getMessage());
        }
    }
	
    // 新增單筆請假單
	@PostMapping("/leave-records")
	public ResponseEntity<?> createLeaveRecord(@RequestBody LeaveRecord leaveRecord) {
	    try {
	        
	        groomerService.createRecord(leaveRecord);
	        return ResponseEntity.ok().body("{\"message\": \"新增成功\"}");
	        
	    } catch (Exception e) {
	        log.error("新增請假單失敗", e);
	        return ResponseEntity.badRequest().body("新增失敗：" + e.getMessage());
	    }
	}
	
    // 更新請假單 (如：審核通過、駁回)
	@PutMapping("/leave-records/{id}")
	public ResponseEntity<?> updateLeaveRecord(
	        @PathVariable Integer id, 
	        @RequestBody LeaveRecord leaveRecord) {
	    try {
	      
	        groomerService.updateRecord(id, leaveRecord);
	        
	        return ResponseEntity.ok().body("{\"message\": \"狀態更新成功\"}");
	        
	    } catch (Exception e) {
	        log.error("更新請假單失敗", e);
	        return ResponseEntity.badRequest().body("更新失敗：" + e.getMessage());
	    }
	}
	
    // 搜尋請假紀錄
	@GetMapping("/leave-records/search")
    public ResponseEntity<List<LeaveRecoredGroomerView>> searchLeaveRecords(
            @RequestParam(required = false) Integer groomerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate leaveStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate leaveEndDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createEndDate) {
        
        List<LeaveRecoredGroomerView> result = groomerService.searchLeaveRecords(
                groomerId, leaveStartDate, leaveEndDate, createStartDate, createEndDate);
        
        return ResponseEntity.ok(result);
    }

}
