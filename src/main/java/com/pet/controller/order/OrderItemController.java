package com.pet.controller.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pet.model.order.Order;
import com.pet.model.order.OrderItem;
import com.pet.service.order.OrderItemService;
import com.pet.service.order.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/ordersItem")
public class OrderItemController {
	
	@Autowired
	private OrderItemService oiService;
	
	@GetMapping("/list")
	public String orderlist(Model model) {
		List<OrderItem> orderItemsList = oiService.getAllOrderItem();
        model.addAttribute("orderItemsList", orderItemsList);
        orderItemsList.forEach(oi -> {
            System.out.println("ItemId=" + oi.getProductItemId() +
                               ", OrderId=" + (oi.getOrder() != null ? oi.getOrder().getOrderId() : "null"));
        });

        return "orderItemsList";
	}
	@GetMapping("/orderId")
	public String orderItemsOrderlist(@RequestParam Integer orderId,Model model) {
		List<OrderItem> orderItemsList = oiService.getOrderItemByorder(orderId);
		model.addAttribute("orderItemsList", orderItemsList);
		return "orderItemsList";
	}
	
}
