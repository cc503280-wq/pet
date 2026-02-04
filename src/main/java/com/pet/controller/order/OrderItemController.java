package com.pet.controller.order;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pet.dto.order.backendOrderItemDTO;
import com.pet.model.order.OrderItem;
import com.pet.service.order.OrderItemService;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/ordersItem")
public class OrderItemController {
	
	@Autowired
	private OrderItemService oiService;
	
	@GetMapping("/list")
	public String orderlist(Model model) {
		List<OrderItem> orderItemsList = oiService.getAllOrderItem();
        model.addAttribute("orderItemsList", orderItemsList);
        return "orderItemsList";
	}
	@GetMapping("/orderId")
	public String orderItemsOrderlist(@RequestParam Integer orderId,Model model) {
		List<OrderItem> orderItemsList = oiService.getOrderItemByorder(orderId);
		model.addAttribute("orderItemsList", orderItemsList);
		return "orderItemsList";
	}
	@GetMapping("/csv")
	public void exportCsv(
	    @RequestParam(required = false) Integer orderId,
	    HttpServletResponse response
	) throws IOException {

	    response.setContentType("text/csv; charset=UTF-8");
	    response.setHeader(
	        "Content-Disposition",
	        "attachment; filename=order_items.csv"
	    );
	    response.getWriter().write("\uFEFF");
	    

	    String csv =
	        oiService.generateCsv(orderId);

	    response.getWriter().write(csv);
	    response.getWriter().flush();
	}
	@GetMapping("/json")
	public void downloadJson(
	        @RequestParam(required = false) Integer orderId,
	        HttpServletResponse response) throws IOException {
			List<OrderItem> items = null;
		if(orderId!=null) {
			 items = oiService.getOrderItemByorder(orderId);
		}else {
			 items = oiService.getAllOrderItem();
		}
	    

		List<backendOrderItemDTO> dtoList = items.stream()
		        .<backendOrderItemDTO>map(item -> new backendOrderItemDTO(
		                item.getProductItemId(),
		                item.getOrder().getOrderId(),
		                item.getProductId(),
		                item.getProductName(),
		                item.getQuantity(),
		                item.getUnitPrice(),
		                item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
		        ))
		        .collect(Collectors.toList());

	    response.setContentType("application/json; charset=UTF-8");
	    response.setHeader("Content-Disposition", "attachment; filename=order_items.json");

	    // Jackson 直接寫入 Response
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.writeValue(response.getOutputStream(), dtoList);
	}
	
}
