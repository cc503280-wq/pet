package com.pet.controller.appointment;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.pet.model.appointment.ServiceItem;
import com.pet.service.appointment.ServiceItemService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/serviceitems")
@Slf4j
public class ServiceItemController {
	@Autowired
	private ServiceItemService serviceItemService;
	

	@GetMapping
	public List<ServiceItem> getAllServiceItems() {
		log.info("取得所有服務項目");
		return serviceItemService.getAllServiceItems();
	}

	@PutMapping("/{id}/toggle")
	public ResponseEntity<?> toggleStatus(@PathVariable Integer id, @RequestParam(required = false) String type) {
		if (type == null) {
			return ResponseEntity.badRequest().body("有問題，請找技術人員");
		}

		try {
			boolean result = serviceItemService.toggleField(id, type);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/insert")
	public ResponseEntity<?> insertGroomer(
	        @RequestParam("serviceName") String serviceName,
	        @RequestParam("targetPetType") String targetPetType,
	        @RequestParam("targetPetSize") String targetPetSize,
	        @RequestParam("isAddon") Boolean isAddon,
	        @RequestParam("durationMinutes") Integer durationMinutes,
	        @RequestParam("price") BigDecimal price,
	        @RequestParam("description") String description,
	        @RequestParam(value = "file", required = false) MultipartFile file) {
	    try {
	         ServiceItem serviceItem = new ServiceItem();       
	         
	         serviceItem.setServiceName(serviceName);
	         serviceItem.setTargetPetType(targetPetType);
	         serviceItem.setTargetPetSize(targetPetSize);
	         serviceItem.setIsAddon(isAddon);
	         serviceItem.setDurationMinutes(durationMinutes);
	         serviceItem.setPrice(price);
	         serviceItem.setDescription(description);
	         
	        serviceItemService.saveServiceItemInfo(serviceItem,file);
	        return ResponseEntity.ok().body("服務項目建立成功");
	    } catch (Exception e) {
	        log.error("新增失敗", e);
	        return ResponseEntity.badRequest().body("預約失敗：" + e.getMessage());
	    }
	}
	
	@GetMapping("/{id}")
    public ResponseEntity<?> getServiceItemById(@PathVariable Integer id) {
        try {
            ServiceItem serviceItem = serviceItemService.getServiceItemById(id);
            return ResponseEntity.ok(serviceItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("查詢失敗：" + e.getMessage());
        }
    }
	
	@PostMapping("/update/{id}") 
    public ResponseEntity<?> updateGroomerInfo(
            @PathVariable Integer id, 
            @RequestParam("serviceName") String serviceName,
	        @RequestParam("targetPetType") String targetPetType,
	        @RequestParam("targetPetSize") String targetPetSize,
	        @RequestParam("isAddon") Boolean isAddon,
	        @RequestParam("durationMinutes") Integer durationMinutes,
	        @RequestParam("price") BigDecimal price,
	        @RequestParam("description") String description,
	        @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
          
        	ServiceItem serviceItem = new ServiceItem();       
	         
	         serviceItem.setServiceName(serviceName);
	         serviceItem.setTargetPetType(targetPetType);
	         serviceItem.setTargetPetSize(targetPetSize);
	         serviceItem.setIsAddon(isAddon);
	         serviceItem.setDurationMinutes(durationMinutes);
	         serviceItem.setPrice(price);
	         serviceItem.setDescription(description);

            
	        serviceItemService.updateServiceItemInfo(id,serviceItem,file);
        
            
            return ResponseEntity.ok().body("{\"message\": \"服務項目資料更新成功\"}");
        } catch (Exception e) {
            log.error("更新失敗", e);
            return ResponseEntity.badRequest().body("更新失敗：" + e.getMessage());
        }
    }
	
	@GetMapping("/search")
	public List<ServiceItem> searchServiceItems(
	    @RequestParam(required = false) String serviceName, 
	    @RequestParam(required = false) String targetPetType, 
	    @RequestParam(required = false) String targetPetSize, 
	    @RequestParam(required = false) Boolean isAddon, 
	    @RequestParam(required = false) Integer isActive 
	) {
	    try {
	        log.info("執行複合搜尋: name={}, type={}, size={}, addon={}, active={}", 
	                  serviceName, targetPetType, targetPetSize, isAddon, isActive);
	        return serviceItemService.searchServiceItems(serviceName, targetPetType, targetPetSize, isAddon, isActive);
	    } catch (Exception e) {
	        log.error("搜尋失敗", e);
	        return null;
	    }
	}
	
	
	
	
	
	
	

}
