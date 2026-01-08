package com.pet.dao.appointment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.appointment.ServiceItem;


public interface ServiceItemRepository extends JpaRepository<ServiceItem, Integer>,JpaSpecificationExecutor<ServiceItem> {

	List<ServiceItem> findByTargetPetTypeAndTargetPetSizeInAndIsActiveTrue(String petType, List<String> petSize);
	
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
	
	


