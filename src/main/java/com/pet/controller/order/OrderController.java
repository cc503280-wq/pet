
package com.pet.controller.order;


import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.pet.model.order.Order;
import com.pet.model.order.OrderItem;
import com.pet.service.member.CouponUsersRealService;
import com.pet.service.member.CouponUsersService;
import com.pet.service.order.OrderItemService;
import com.pet.service.order.OrderService;
import com.pet.service.order.ShipmentService;
import com.pet.service.product.ProductService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/orders")
public class OrderController {
	
	@Autowired
	private OrderService oService;
	@Autowired
	private ShipmentService shipmentService;
	@Autowired
	private OrderItemService oItemService;
	
	@Autowired
	private ProductService pService;
	@Autowired
	private CouponUsersRealService couponUsersRealService;
	@Autowired
	private CouponUsersService cUsersService;
	
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
	    //修改庫存量
	    List<OrderItem> orders = oItemService.getOrderItemByorder(orderId);
	    for (OrderItem orderItem : orders) {
	    	pService.updateProductStock(orderId, orderItem.getQuantity(), 0);
		}
	    //修改優惠券
	    Order order = oService.getOrderById(orderId);
	    couponUsersRealService.CouponUsersUpdate(couponUsersRealService.couponUserId(order.getMemberId(), order.getCouponId()), "unused", LocalDate.now());
	    
	    
	    return ResponseEntity.ok().build();
	}
	
	
	
	

}
