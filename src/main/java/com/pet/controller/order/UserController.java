package com.pet.controller.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.pet.controller.member.AdminAuthController;
import com.pet.model.order.Order;
import com.pet.model.order.OrderItem;
import com.pet.service.order.OrderItemService;
import com.pet.service.order.OrderService;
import com.pet.service.product.ProductService;
import com.pet.util.LoginUser;

@RestController
@RequestMapping("/shop/userOrder")
public class UserController {

    private final AdminAuthController adminAuthController;
	@Autowired
	private OrderService oService;
	
	@Autowired
	private OrderItemService oiService ;
	
	@Autowired
	private ProductService pService;

    UserController(AdminAuthController adminAuthController) {
        this.adminAuthController = adminAuthController;
    }
	
	@GetMapping("/list")
	@ResponseBody
	public List<Order> userOrderlist(@LoginUser Integer userId) {
		if(userId==null) {
			return new ArrayList<>();
		}
		return oService.getUserOrder(userId);
	}
	
	@GetMapping("/firstName")
	@ResponseBody
	public String getFirstProductName(Integer orderId) {
		String productName =pService.getProductById(oiService.getOrderItemByorder(orderId).get(0).getProductId()).getProductName();
		return productName;
	}
	
	@GetMapping("/detail")
	@ResponseBody
	public List<OrderItem> userOrderItems(Integer orderId){
		return null;
	}
}
