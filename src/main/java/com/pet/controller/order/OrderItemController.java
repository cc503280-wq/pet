package com.pet.controller.order;

import java.io.Console;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
	    
	    // 統計各商品銷售額占比
	    Map<String, Double> salesMap = orderItemsList.stream()
	        .collect(Collectors.groupingBy(
	            OrderItem::getProductName,
	            Collectors.summingDouble(item -> item.getSubtotal().doubleValue())
	        ));
	    
	    System.out.println("後端統計結果: " + salesMap);
	    
	    model.addAttribute("orderItemsList", orderItemsList);
	    // 【關鍵修正】：將整個 Map 傳給前端，名稱要跟 JS 裡的 /*[[${salesSummary}]]*/ 對齊
	    model.addAttribute("salesSummary", salesMap); 
	    
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
	@GetMapping("/product")
	public String orderItemsProductlist(@RequestParam Integer productId,Model model) {
		List<OrderItem> orderItemsList = oiService.getOrderItemByProductId(productId);
		// 按訂單日期排序 (由舊到新)
	    orderItemsList.sort(Comparator.comparing(item -> item.getOrder().getOrderDate()));

	    // 準備圖表數據
	    List<String> labels = orderItemsList.stream()
	            .map(item -> item.getOrder().getOrderDate().toLocalDate().toString())
	            .collect(Collectors.toList());
	    System.out.println("準備圖表數據:"+labels);
	            
	    List<Integer> data = orderItemsList.stream()
	            .map(OrderItem::getQuantity)
	            .collect(Collectors.toList());
	    // ✅ 取得商品名稱
	    String productName = null;
	    if (!orderItemsList.isEmpty()) {
	        productName = orderItemsList.get(0).getProductName();
	    }
	    System.out.println("準備圖表數據:"+data);
		model.addAttribute("orderItemsList", orderItemsList);
		model.addAttribute("chartLabels", labels); // 日期標籤
	    model.addAttribute("chartData", data);     // 銷售數量
	    model.addAttribute("productName", productName); 
	    orderItemsList.forEach(item -> 
	    System.out.println("PID=" + item.getProductId() + ", NAME=" + item.getProductName())
	);
		return "orderItemsList";
	}
	
}
