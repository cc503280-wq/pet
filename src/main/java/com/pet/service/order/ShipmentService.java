package com.pet.service.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pet.dao.order.ShipmentRepository;
import com.pet.model.order.Order;
import com.pet.model.order.Shipment;

import tools.jackson.databind.ObjectMapper;

@Service
public class ShipmentService {
	@Autowired
	private ShipmentRepository sRepository;
	@Autowired
    private ObjectMapper objectMapper;

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
		if (id != null) {
            return sRepository.findByOrder_OrderId(id);
        }
		return sRepository.findAll();
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
	public String generateShipmentCsv(Integer orderId) {
        List<Shipment> shipments = getShipmentByOrderId(orderId);

        StringBuilder sb = new StringBuilder();

        // CSV 標題
        sb.append("物流編號,訂單編號,物流方式,物流運費,物流追蹤碼,寄送日期,到達日期,物流狀態,收件人姓名,收件人電話,收件人地址\n");

        // 資料列
        for (Shipment s : shipments) {
            sb.append(s.getShipmentId()).append(",")
              .append(s.getOrder().getOrderId()).append(",")
              .append(s.getShippingMethod()).append(",")
              .append(s.getShippingFee()).append(",")
              .append(s.getTrackingNumber()).append(",")
              .append(s.getShippedAt()).append(",")
              .append(s.getDeliveredAt()).append(",")
              .append(s.getStatus()).append(",")
              .append(s.getRecipientName()).append(",")
              .append(s.getRecipientPhone()).append(",")
              .append(s.getShippingAddress())
              .append("\n");
        }

        return sb.toString();
    }
	public String generateShipmentJson(Integer orderId) throws JsonProcessingException {
        List<Shipment> shipments = getShipmentByOrderId(orderId);

        // 將 list 轉成 JSON 字串
        return objectMapper.writeValueAsString(shipments);
    }

}
