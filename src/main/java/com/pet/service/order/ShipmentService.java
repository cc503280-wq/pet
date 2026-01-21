package com.pet.service.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dao.order.ShipmentRepository;
import com.pet.model.order.Order;
import com.pet.model.order.Shipment;

@Service
public class ShipmentService {
	@Autowired
	private ShipmentRepository sRepository;

	// 找全部運輸資訊
	public List<Shipment> getAllShipments() {
		return sRepository.findAll();
	}

	// 找單筆運輸資訊根據運輸編號
	public Shipment getShipmentsById(Integer id) {
		return sRepository.getById(id);
	}

	// 根據訂單編號尋找運輸資訊
	public List<Shipment> getShipmentByOrderId(Integer id) {
		return sRepository.findByOrder_OrderId(id);
	}
	@Transactional
	public String insertShipment(Shipment shipment) {
		sRepository.save(shipment);
		return "insert OK";
	}
	@Transactional
	public String updateOrder(Shipment shipment) {
		sRepository.save(shipment);
		return "update OK";
	}
	@Transactional
	public void updateShipmentStatus(Integer orderId,String status) {
		List<Shipment> shipments = sRepository.findByOrder_OrderId(orderId);
		for (Shipment shipment : shipments) {
			shipment.setStatus(status);
		}
	}
	@Transactional
	public Shipment updateShipment(Shipment shipment){
		return sRepository.save(shipment);
	}

}
