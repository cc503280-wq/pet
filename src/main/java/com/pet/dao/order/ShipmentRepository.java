package com.pet.dao.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.order.Order;
import com.pet.model.order.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
	 List<Shipment> findByOrder_OrderId(Integer orderId);
}
