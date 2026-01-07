package com.pet.service.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dao.order.OrderItemRepository;

import com.pet.model.order.OrderItem;

@Service
public class OrderItemService {
	@Autowired
	private OrderItemRepository oiRepository;

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

}
