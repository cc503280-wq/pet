package com.pet.dao.appointment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.appointment.ServiceItem;


/**
 * ServiceItemRepository: 服務項目存取層
 * 負責：查詢上架服務、條件篩選 (名稱/寵物類型/體型/是否加購)
 */
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Integer>,JpaSpecificationExecutor<ServiceItem> {

    // 查詢所有「上架中」(Active) 的服務
	List<ServiceItem> findByIsActiveTrue();
	
    // 依寵物類型與體型查詢 (前台預約時使用)
	List<ServiceItem> findByTargetPetTypeAndTargetPetSizeInAndIsActiveTrue(String petType, List<String> petSize);
	
    /**
     * 服務項目複合搜尋 (後台管理用)
     * 支援：名稱(模糊)、寵物類型、體型、是否加購、是否上架
     */
	@Query("SELECT s FROM ServiceItem s WHERE " +
		       "(:name IS NULL OR s.serviceName LIKE %:name%) AND " +
		       "(:petType IS NULL OR s.targetPetType = :petType) AND " +
		       "(:petSize IS NULL OR s.targetPetSize = :petSize) AND " +
		       "(:isAddon IS NULL OR s.isAddon = :isAddon) AND " +
		       "(:isActive IS NULL OR s.isActive = :isActive)")
		List<ServiceItem> complexSearch(
		    @Param("name") String name,
		    @Param("petType") String petType,
		    @Param("petSize") String petSize,
		    @Param("isAddon") Boolean isAddon,
		    @Param("isActive") Boolean isActive
		);
	
}
