package com.pet.service.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dao.order.OrderItemRepository;
import com.pet.dao.order.OrderRepository;
import com.pet.dao.product.ProductRepository;

import com.pet.model.order.Order;
import com.pet.model.product.Product;
import com.pet.aspect.LogAction;

@Service
public class OrderService {
	@Autowired
	private OrderRepository oRepository;

	@Autowired
	private ProductRepository pRepository;

	@Autowired
	private OrderItemRepository oiRepository;

	// 找全部訂單
	public List<Order> getAllOrders() {
		return oRepository.findAll();
	}

	// 找單筆訂單透過訂單編號
	public Order getOrderById(Integer id) {
		return oRepository.getById(id);
	}

	public List<Order> getUserOrder(Integer id) {

		return oRepository.findByMemberId(id);
	}

	// 找多筆透過會員編號
	public List<Order> getOrderByMemberId(Integer id) {
		return oRepository.findByMemberId(id);
	}

	@LogAction(type = LogAction.ActionType.CREATE_ORDER) // [AOP] 紀錄訂單建立
	public Order insertOrder(Order order) {
		return oRepository.save(order);
	}

	public String updateOrder(Order order) {
		oRepository.save(order);
		return "update OK";
	}

	@Transactional
	public boolean updateOrderStatus(Integer orderId, String status) {
		return oRepository.findById(orderId)
				.map(order -> {
					order.setStatus(status); // Lombok 自動生成 setter
					return true;
				})
				.orElse(false);
	}

}
