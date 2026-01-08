package com.pet.dao.order;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.order.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByMemberId(Integer memberId);
}
