
package com.pet.controller.order;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pet.model.order.Order;
import com.pet.model.order.OrderItem;
import com.pet.service.member.CouponUsersRealService;
import com.pet.service.member.CouponUsersService;
import com.pet.service.member.MemberService;
import com.pet.service.order.OrderItemService;
import com.pet.service.order.OrderService;
import com.pet.service.order.ShipmentService;
import com.pet.service.product.ProductService;
import com.pet.aspect.LogAction;

import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@CrossOrigin
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
	@Autowired
	private MemberService mService;

	@GetMapping("/list")
	public String orderlist(Model model) {
		List<Order> orders = oService.getAllOrders();
		model.addAttribute("orders", orders);
		// 加入這一行！
		addChartAttributes(model, null);
		return "orderList";
	}

	@GetMapping("/memberId")
	public String memberOrderlist(@RequestParam Integer memberId, Model model) {
		List<Order> orders = oService.getOrderByMemberId(memberId);
		model.addAttribute("orders", orders);
		// 加入這一行！並傳入 memberId
		addChartAttributes(model, memberId);
		return "orderList";
	}

	@PostMapping("/updateOrderStatus")
	public ResponseEntity<String> updateOrderStatus(
			@RequestParam Integer orderId,
			@RequestParam String status) {

		boolean success = oService.updateOrderStatus(orderId, status);
		if (success) {
			return ResponseEntity.ok("更新成功");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("訂單不存在");
		}
	}

	@PostMapping("/cancel")
	@Transactional
	public ResponseEntity<Void> cancelOrder(@RequestParam Integer orderId) {
		Order order = oService.getOrderById(orderId);
		if (order.getStatus().equals("已取消")) {
			return ResponseEntity.badRequest().build();
		}

		oService.updateOrderStatus(orderId, "已取消");
		shipmentService.updateShipmentStatus(orderId, "已取消");
		// 修改庫存量
		List<OrderItem> orders = oItemService.getOrderItemByorder(orderId);
		for (OrderItem orderItem : orders) {
			pService.updateProductStock(orderId, 0, orderItem.getQuantity());
		}
		// 修改優惠券

		if (order.getCouponId() != null) {
			couponUsersRealService.rollbackCouponStatus(order.getMemberId(), order.getCouponId());
		}
		// 回滾會員幣
		mService.updateMemberPoints(order.getMemberId(), order.getUsePoints(), order.getGetPoints());

		return ResponseEntity.ok().build();
	}

	@PostMapping("/usercancel")
	@Transactional
	@LogAction(type = LogAction.ActionType.CANCEL_ORDER)
	public ResponseEntity<Void> userCancelOrder(@RequestParam Integer orderId) {
		Order order = oService.getOrderById(orderId);
		if (order.getStatus().equals("已取消")) {
			return ResponseEntity.badRequest().build();
		}

		oService.updateOrderStatus(orderId, "已取消");
		shipmentService.updateShipmentStatus(orderId, "已取消");
		// 修改庫存量
		List<OrderItem> orders = oItemService.getOrderItemByorder(orderId);
		for (OrderItem orderItem : orders) {
			pService.updateProductStock(orderId, 0, orderItem.getQuantity());
		}
		// 修改優惠券

		if (order.getCouponId() != null) {
			couponUsersRealService.rollbackCouponStatus(order.getMemberId(), order.getCouponId());
		}
		// 回滾會員幣
		mService.updateMemberPoints(order.getMemberId(), order.getUsePoints(), 0);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/csv")
	public void exportOrdersCsv(@RequestParam(required = false) Integer memberId,
			HttpServletResponse response) throws IOException {

		String csvContent = oService.generateOrdersCsv(memberId);

		response.setContentType("text/csv; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=orders.csv");
		response.getWriter().write("\uFEFF");

		response.getWriter().write(csvContent);
	}

	@GetMapping("/json")
	public void exportOrdersJson(@RequestParam(required = false) Integer memberId,
			HttpServletResponse response) throws IOException {

		List<Order> orders = oService.getOrderByMemberId(memberId);

		// 設定下載檔名與 MIME
		response.setContentType("application/json; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=orders.json");

		// 用 Jackson ObjectMapper 寫入 JSON
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), orders);
	}

	private void addChartAttributes(Model model, Integer memberId) {
		List<String> labels = oService.getRecentSixMonthsLabels();
		List<Double> totalSales = oService.getMonthlyTotalSales();
		List<Double> memberSales = new ArrayList<>();

		if (memberId != null) {
			memberSales = oService.getMemberMonthlySales(memberId);
		} else {
			for (int i = 0; i < labels.size(); i++)
				memberSales.add(0.0);
		}

		model.addAttribute("chartLabels", labels);
		model.addAttribute("totalSalesData", totalSales);
		model.addAttribute("memberSalesData", memberSales);
	}

}
