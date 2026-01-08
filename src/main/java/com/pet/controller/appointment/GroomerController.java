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

@RestController 
@RequestMapping("/groomers") 
@Slf4j
public class GroomerController {

   	@Autowired
	private GroomerService groomerService;

 
	@GetMapping
	public List<Groomer> getAllGroomer() {
		return groomerService.getAllGroomer();
	}
	
	@GetMapping("/search")
    public ResponseEntity<List<Groomer>> searchGroomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer isActive // 1:在職, 0:離職, null:全部
    ) {
        List<Groomer> result = groomerService.searchGroomers(name, startDate, endDate, isActive);
        return ResponseEntity.ok(result);
    }
	
	
	@PostMapping("/insert")
	public ResponseEntity<?> insertGroomer(
	        @RequestParam("groomerName") String groomerName,
	        @RequestParam("phone") String phone,
	        @RequestParam("email") String email,
	        @RequestParam("hiredate") LocalDate hiredate,
	        @RequestParam(value = "file", required = false) MultipartFile file) {
	    try {
	        Groomer groomer = new Groomer();
	        groomer.setGroomerName(groomerName);
	        groomer.setPhone(phone);
	        groomer.setEmail(email);
	        groomer.setHiredate(hiredate);       

	        groomerService.saveGroomerInfo(groomer,file);
	        return ResponseEntity.ok().body("預約建立成功");
	    } catch (Exception e) {
	        log.error("新增失敗", e);
	        return ResponseEntity.badRequest().body("預約失敗：" + e.getMessage());
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
	
	@PostMapping("/update/{id}") 
    public ResponseEntity<?> updateGroomerInfo(
            @PathVariable Integer id, 
            @RequestParam("groomerName") String groomerName,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("hiredate") LocalDate hiredate,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
          
            Groomer groomer = new Groomer();
            groomer.setGroomerName(groomerName);
            groomer.setPhone(phone);
            groomer.setEmail(email);
            groomer.setHiredate(hiredate);
            groomerService.updateGroomerInfo(id, groomer, file);
            
            return ResponseEntity.ok().body("{\"message\": \"美容師資料更新成功\"}");
        } catch (Exception e) {
            log.error("更新失敗", e);
            return ResponseEntity.badRequest().body("更新失敗：" + e.getMessage());
        }
    }
	
	@PatchMapping("/{id}/status")
    public ResponseEntity<?> updateGroomerStatus(
            @PathVariable Integer id,
            @RequestParam Boolean isActive) {
        try {
            groomerService.updateGroomerStatus(id, isActive);
            return ResponseEntity.ok().body("{\"message\": \"狀態更新成功\"}");
        } catch (Exception e) {
            log.error("狀態更新失敗", e);
            return ResponseEntity.badRequest().body("更新失敗：" + e.getMessage());
        }
    }
	
	//============請假=======//
	
	@GetMapping(path = "/leave-records")
	public List<LeaveRecoredGroomerView> getAllLeaveRecords() {
		return groomerService.getAllLeaveRecords();
	}
	
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
