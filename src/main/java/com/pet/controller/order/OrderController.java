
package com.pet.controller.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pet.model.order.Order;
import com.pet.service.order.OrderService;
import com.pet.service.order.ShipmentService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/orders")
public class OrderController {
	
	@Autowired
	private OrderService oService;
	@Autowired
	private ShipmentService shipmentService;
	
	@GetMapping("/list")
	public String orderlist(Model model) {
		List<Order> orders = oService.getAllOrders();
        model.addAttribute("orders", orders);
        return "orderList";
	}
	@GetMapping("/memberId")
	public String memberOrderlist(@RequestParam Integer memberId,Model model) {
		List<Order> orders = oService.getOrderByMemberId(memberId);
		model.addAttribute("orders", orders);
		return "orderList";
	}
	
	@PostMapping("/updateOrderStatus")
    public ResponseEntity<String> updateOrderStatus(
            @RequestParam Integer orderId,
            @RequestParam String status) {

        boolean success = oService.updateOrderStatus(orderId, status);
        if(success){
            return ResponseEntity.ok("更新成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("訂單不存在");
        }
    }
	@PostMapping("/cancel")
	public ResponseEntity<Void> cancelOrder(@RequestParam Integer orderId){
	    oService.updateOrderStatus(orderId, "已取消");
	    shipmentService.updateShipmentStatus(orderId, "已取消");
	    return ResponseEntity.ok().build();
	}
	
	

}
