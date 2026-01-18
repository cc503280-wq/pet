package com.pet.service.appointment;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pet.dao.appointment.AppointmentRepository;
import com.pet.dao.appointment.ServiceItemRepository;
import com.pet.model.appointment.ServiceItem;
import lombok.extern.slf4j.Slf4j;

/**
 * ServiceItemService: 美容服務項目管理
 * 負責：服務上架/下架、圖片上傳、停用檢查
 */
@Service
@Slf4j
public class ServiceItemService {
	
	@Autowired
	private ServiceItemRepository serviceItemRepository;
	
	@Autowired
	private Cloudinary cloudinary;
	
	@Autowired 
    private AppointmentRepository appointmentRepository; 
	
	// 取得所有服務項目 (後台用)
	public List<ServiceItem> getAllServiceItems() {
		return serviceItemRepository.findAll();
	}
	
	// 取得所有 "上架中" 服務項目 (前台用)
	public List<ServiceItem> getAllActiveServiceItems() {
		return serviceItemRepository.findByIsActiveTrue();
	}
	    
	public ServiceItem getServiceItemById(Integer id) {
        return serviceItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的美容師")); // 註：這裡原本錯誤訊息是美容師，建議改為服務項目
    }


    // 新增服務項目 (含圖片)
	@Transactional
    public ServiceItem saveServiceItemInfo(ServiceItem serviceItem, MultipartFile file) throws IOException {
        log.info("開始新增美容師: {}", serviceItem.getServiceName());

        if (file != null && !file.isEmpty()) {
            String imageUrl = saveFile(file); 
            serviceItem.setPicture(imageUrl);     
        }
        
        ServiceItem savedServiceItem = serviceItemRepository.save(serviceItem);

        return savedServiceItem;
    }
	
	
    // 更新服務項目
	@Transactional
    public ServiceItem updateServiceItemInfo(Integer id, ServiceItem inputserviceItem, MultipartFile file) throws IOException {
        log.info("開始修改服務項目: {}", inputserviceItem.getServiceName());
        
        ServiceItem existing = serviceItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的美容師"));

        existing.setServiceName(inputserviceItem.getServiceName());
        existing.setTargetPetType(inputserviceItem.getTargetPetType());
        existing.setTargetPetSize(inputserviceItem.getTargetPetSize());
        existing.setIsAddon(inputserviceItem.getIsAddon());
        existing.setDurationMinutes(inputserviceItem.getDurationMinutes());
        existing.setPrice(inputserviceItem.getPrice());
        existing.setDescription(inputserviceItem.getDescription());
        
        if (file != null && !file.isEmpty()) {
            String imageUrl = saveFile(file); 
            log.info("儲存圖片路徑進ServiceItem existing");
            existing.setPicture(imageUrl);     
            
        }
        ServiceItem save = serviceItemRepository.save(existing);
        log.info("儲存成功");
        return save;
    }

    // 圖片上傳邏輯
    private String saveFile(MultipartFile file) throws IOException {
        Map params = ObjectUtils.asMap(
            "folder", "serviceItem_pictures",  
            "use_filename", true,
            "unique_filename", true
        );
        
        // 上傳到 Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        
        // 回傳 HTTPS 網址
        return (String) uploadResult.get("secure_url");
    }
	
	
    /**
     * 切換服務項目的狀態 (active=上下架)
     * 在下架前，必須檢查是否還有未完成的預約單使用此服務
     */
    public boolean toggleField(Integer id, String fieldType) {
        return serviceItemRepository.findById(id)
            .map(item -> {
                boolean result = false;
                if ("active".equals(fieldType)) {
                   
                    boolean nextStatus = !Boolean.TRUE.equals(item.getIsActive());
                    
                    // 如果是要「下架」(nextStatus = false)
                    if (!nextStatus) { 
                        
                        LocalDate today = LocalDate.now();
                                              
                        // 查詢該服務在今日之後是否還有 "Active" 的預約單
                        long conflictCount = appointmentRepository.countActiveAppointmentsByServiceId(id, today);
                        
                        // 若有衝突，則拋出異常阻止下架
                        if (conflictCount > 0) {
                            throw new RuntimeException(
                                "無法下架！該服務目前尚有 " + conflictCount + " 筆未執行的預約單 (含今日)。" +
                                "請先至預約管理手動取消或修改這些訂單。"
                            );
                        }
                    }
                    
                    item.setIsActive(nextStatus);
                    result = nextStatus;
                }
                
                serviceItemRepository.save(item);
                return result; 
            })
            .orElseThrow(() -> new RuntimeException("找不到服務項目 ID: " + id));
    }
    
    // 複合搜尋
    public List<ServiceItem> searchServiceItems(String name, String petType, String petSize, Boolean isAddon, Integer status) {
        Boolean isActive = null;
        if (status != null) {
            isActive = (status == 1); 
        }
        
        return serviceItemRepository.complexSearch(name, petType, petSize, isAddon, isActive);
    }
	
}
