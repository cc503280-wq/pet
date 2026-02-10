package com.pet.controller.order;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.pet.controller.member.AdminAuthController;
import com.pet.dto.order.OrderItemDTO;
import com.pet.model.order.Order;
import com.pet.model.order.OrderItem;
import com.pet.model.order.Shipment;
import com.pet.service.member.MemberService;
import com.pet.service.order.OrderItemService;
import com.pet.service.order.OrderService;
import com.pet.service.order.ShipmentService;
import com.pet.service.product.ProductService;
import com.pet.util.LoginUser;
import org.springframework.web.bind.annotation.RequestParam;


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
	
	@Autowired
	private ShipmentService shipmentService;
	@Autowired
	private MemberService mService;

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
	public List<OrderItemDTO> userOrderItems(Integer orderId){
		return oiService.getOrderItemDTO(orderId);
	}
	
	@GetMapping("/shipment")
	public Shipment getMethodName(Integer orderId) {
		return shipmentService.getShipmentByOrderId(orderId).get(0);
	}
	@PostMapping("/complete")
	@ResponseBody
	public boolean completeOrder(Integer orderId) {
		Order order = oService.getOrderById(orderId);
		//修改會員幣
		mService.updateMemberPoints(order.getMemberId(),0,order.getGetPoints());
		
		//修改訂單狀態
		return oService.updateOrderStatus(orderId, "已完成訂單");
	}
	
	
	
}
