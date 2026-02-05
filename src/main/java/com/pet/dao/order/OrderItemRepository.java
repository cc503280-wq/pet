package com.pet.dao.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pet.model.order.Order;
import com.pet.model.order.OrderItem;


public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
	List<OrderItem> findByOrder_OrderId(Integer orderId);
	List<OrderItem> findByProductId(Integer productId);
}
