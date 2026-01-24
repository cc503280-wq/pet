package com.pet.service.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dao.order.OrderItemRepository;
import com.pet.dto.order.OrderItemDTO;
import com.pet.model.order.OrderItem;
import com.pet.model.order.Shipment;
import com.pet.model.product.Product;
import com.pet.service.product.ProductService;


@Service
public class OrderItemService {
	@Autowired
	private OrderItemRepository oiRepository;
	@Autowired
	private ProductService pService;
	@Autowired
	private ShipmentService shipmentService;

	// 找全部訂單明細
	public List<OrderItem> getAllOrderItem() {
		return oiRepository.findAll();
	}

	// 根據訂單明細編號搜尋
	public OrderItem getOrderItem(Integer id) {
		return oiRepository.getById(id);
	}

	//根據訂單編號查詢明細
	public List<OrderItem> getOrderItemByorder(Integer id) {
		return oiRepository.findByOrder_OrderId(id);
	}
	public OrderItem insertOrderItem(OrderItem orderItem) {
		return oiRepository.save(orderItem);
	}
	
	//將訂單細項與商品資料合成一個DTO
	public List<OrderItemDTO> getOrderItemDTO(Integer orderId) {
		List<OrderItem> orderItems = oiRepository.findByOrder_OrderId(orderId);
		List<OrderItemDTO> list = new ArrayList<OrderItemDTO>();
		
		for (OrderItem orderItem : orderItems) {
			OrderItemDTO orderItemDTO = new OrderItemDTO();
			Product product = pService.getProductById(orderItem.getProductId());
			List<Shipment> shipment =shipmentService.getShipmentByOrderId(orderId);
			orderItemDTO.setOrderItem(orderItem);
			orderItemDTO.setProduct(product);
			orderItemDTO.setShipment(shipment.get(0));
			list.add(orderItemDTO);
		}
		return list;
	}

}
