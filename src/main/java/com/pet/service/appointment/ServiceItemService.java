package com.pet.service.appointment;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    // 取得所有服務項目 (後台用)
    public List<ServiceItem> getAllServiceItems() {
        return serviceItemRepository.findAll();
    }

    // 取得所有 "上架中" 服務項目 (前台用)
    // 開啟Redis快取
    // value = "serviceItems" -> Redis 裡的分類名稱
    // key = "'activeList'" -> Redis 裡的 Key 名稱
    @Cacheable(value = "serviceItems", key = "'activeList'")
    public List<ServiceItem> getAllActiveServiceItems() {
        log.info("--- (這行只會出現一次) 從 SQL 資料庫查詢 Active 服務列表 ---");
        return serviceItemRepository.findByIsActiveTrue();
    }

    public ServiceItem getServiceItemById(Integer id) {
        return serviceItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的服務項目"));
    }

    // 新增服務項目 (含圖片)
    @Transactional
    public ServiceItem saveServiceItemInfo(ServiceItem serviceItem, MultipartFile file) throws IOException {
            log.info("開始新增服務: {}", serviceItem.getServiceName());
            if (file != null && !file.isEmpty()) {
                    String imageUrl = saveFile(file);
                    serviceItem.setPicture(imageUrl);
                }
            
                ServiceItem saved = serviceItemRepository.save(serviceItem);
                log.info("服務項目已寫入資料庫: ID={}", saved.getServiceId());
                clearCacheAfterCommit();

        return saved;
    }


    private String saveFile(MultipartFile file) throws IOException {
        Map params = ObjectUtils.asMap(
            "folder", "serviceItem_pictures",  
            "use_filename", true,
            "unique_filename", true
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) uploadResult.get("secure_url");
    }
    
    
    /**
     * 切換服務項目的狀態 (active=上下架)
     * 在下架前，必須檢查是否還有未完成的預約單使用此服務
     */
    @Transactional
    public boolean toggleField(Integer id, String fieldType) {     
        // 1. 先把資料查出來
        ServiceItem item = serviceItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到服務項目 ID: " + id));

        boolean nextStatus = false;

        // 2. 判斷是否為 active 狀態切換
        if ("active".equals(fieldType)) {
            nextStatus = !Boolean.TRUE.equals(item.getIsActive());

            // 3. 如果是「下架」，檢查預約衝突
            if (!nextStatus) { 
                LocalDate today = LocalDate.now();
                long conflictCount = appointmentRepository.countActiveAppointmentsByServiceId(id, today);
                if (conflictCount > 0) {
                    throw new RuntimeException("無法下架！尚有預約未完成，筆數: " + conflictCount);
                }
            }
            
 
            item.setIsActive(nextStatus);
            serviceItemRepository.saveAndFlush(item);
            
            clearCacheAfterCommit();
          
        }
        
        return nextStatus;
    }

    

	// 複合搜尋
    public List<ServiceItem> searchServiceItems(String name, String petType, String petSize, Boolean isAddon,
            Integer status) {
        Boolean isActive = null;
        if (status != null) {
            isActive = (status == 1);
        }

        return serviceItemRepository.complexSearch(name, petType, petSize, isAddon, isActive);
    }
    
    
    //清除Redis指定Key的資料
    private void clearCacheAfterCommit() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) { //判斷是否正在進行Transaction
        	//new TransactionSynchronization() 建立一個任務物件 https://blog.csdn.net/weixin_44313584/article/details/136825850
        	//registerSynchronization: 註冊
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
               
            	//當Transaction Commit結束後才執行
            	@Override
                public void afterCommit() {
                    try {
                        log.info("SQL 交易已提交，準備強制刪除 Redis Key [serviceItems::activeList] <<");
                        
                        //直接指定 Key 名稱刪除 "serviceItems::activeList"
                        Boolean result = redisTemplate.delete("serviceItems::activeList");
                        
                        log.info("Redis Key 刪除結果: {}", result);
                    } catch (Exception e) {
                        log.warn("Redis 清除快取失敗 (Redis 可能離線，不影響業務): {}", e.getMessage());
                        // 不拋出例外，讓業務繼續正常運作
                    }
                }
            });
        } else {
            try {
                redisTemplate.delete("serviceItems::activeList");
            } catch (Exception e) {
                log.warn("Redis 清除快取失敗 (Redis 可能離線): {}", e.getMessage());
            }
        }
    }

 

}
