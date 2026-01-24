package com.pet.dto.order;

import com.pet.model.order.OrderItem;
import com.pet.model.order.Shipment;
import com.pet.model.product.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
	private Product product;
	private OrderItem orderItem;
	private Shipment shipment;
}
