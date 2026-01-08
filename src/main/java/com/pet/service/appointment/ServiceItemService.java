package com.pet.service.appointment;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pet.dao.appointment.ServiceItemRepository;
import com.pet.model.appointment.ServiceItem;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServiceItemService {
	
	@Autowired
	private ServiceItemRepository serviceItemRepository;
	
	@Autowired
	private Cloudinary cloudinary;
	
	
	public List<ServiceItem> getAllServiceItems() {
		return serviceItemRepository.findAll();
	}
	    
	public ServiceItem getServiceItemById(Integer id) {
        return serviceItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的美容師"));
    }


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
	
	
	@Transactional
    public ServiceItem updateServiceItemInfo(Integer id, ServiceItem inputserviceItem, MultipartFile file) throws IOException {
        log.info("開始新增美容師: {}", inputserviceItem.getServiceName());
        
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
	
	
    public boolean toggleField(Integer id, String fieldType) {
        return serviceItemRepository.findById(id)
            .map(item -> {
                boolean result = false;
                if ("active".equals(fieldType)) {
                   
                    boolean nextStatus = !Boolean.TRUE.equals(item.getIsActive());
                    item.setIsActive(nextStatus);
                    result = nextStatus;
                }
                
                /* TODO: 
                   如果該服務已被預約，是否允許下架？
                   這部分建議先執行查詢，若有未完成預約則拋出 RuntimeException
                */
                
                serviceItemRepository.save(item);
                return result; 
            })
            .orElseThrow(() -> new RuntimeException("找不到服務項目 ID: " + id));
    }
    
    public List<ServiceItem> searchServiceItems(String name, String petType, String petSize, Boolean isAddon, Integer status) {
        Boolean isActive = null;
        if (status != null) {
            isActive = (status == 1); 
        }
        
        return serviceItemRepository.complexSearch(name, petType, petSize, isAddon, isActive);
    }
	
}
