package com.pet.controller.order;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pet.model.order.Shipment;
import com.pet.service.order.EcpayService;
import com.pet.service.order.ShipmentService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/shipment")
public class ShipmentController {


	@Autowired
	private ShipmentService shipmentService;
	@Autowired
    private EcpayService ecpayService;


	
	@GetMapping("/list")
	public String shipmentList(Model model) {
		List<Shipment> shipments = shipmentService.getAllShipments();
		model.addAttribute("shipmentsList",shipments);
		return "shipmentsList";
	}
	@GetMapping("/orderId")
	public String orderItemsOrderlist(@RequestParam Integer orderId,Model model) {
		List<Shipment> shipmentsList = shipmentService.getShipmentByOrderId(orderId);
		model.addAttribute("shipmentsList", shipmentsList);
		return "shipmentsList";
	}
	@GetMapping("/updatePage")
	public String updateShippingPage(@RequestParam Integer shipmentId,Model model) {
		Shipment shipment = shipmentService.getShipmentsById(shipmentId);
		model.addAttribute("s",shipment);
		return "UpdateShipping";
		
	}
	@PostMapping("/update")
	public String updateShipping(Shipment shipment,Model model) {
		shipmentService.updateShipment(shipment);
		List<Shipment> shipments = shipmentService.getAllShipments();
		model.addAttribute("shipmentsList",shipments);
		return "shipmentsList";
	}
	@GetMapping("/csv")
	public void exportShipmentCsv(@RequestParam(required = false) Integer orderId,
	                              HttpServletResponse response) throws IOException {

	    String csvContent = shipmentService.generateShipmentCsv(orderId);

	    response.setContentType("text/csv; charset=UTF-8");
	    response.setHeader("Content-Disposition", "attachment; filename=shipments.csv");
	    response.getWriter().write("\uFEFF");

	    try (PrintWriter writer = response.getWriter()) {
	        writer.write(csvContent);
	        writer.flush();
	    }
	}
	@GetMapping("/json")
	public void exportShipmentJson(@RequestParam(required = false) Integer orderId,
	                               HttpServletResponse response) throws IOException {

	    String jsonContent = shipmentService.generateShipmentJson(orderId);

	    response.setContentType("application/json; charset=UTF-8");
	    response.setHeader("Content-Disposition", "attachment; filename=shipments.json");

	    try (PrintWriter writer = response.getWriter()) {
	        writer.write(jsonContent);
	        writer.flush();
	    }
	}
	@PostMapping("/createEcpay")
    @ResponseBody
    public ResponseEntity<String> createEcpayLogistics(@RequestParam Integer shipmentId) {
        try {
            // 1. 撈取物流單
            Shipment shipment = shipmentService.getShipmentsById(shipmentId);
            if (shipment == null) {
                return ResponseEntity.badRequest().body("找不到該筆物流單");
            }

            // 2. 檢查狀態 (避免重複出貨)
            if (!"備貨中".equals(shipment.getStatus())) {
                return ResponseEntity.badRequest().body("狀態非[備貨中]，無法建立物流單");
            }
            
            // 3. 檢查物流方式 (僅限超商)
            String method = shipment.getShippingMethod();
            if (method == null || "宅配".equals(method)) {
                return ResponseEntity.badRequest().body("宅配請使用黑貓 API 或手動出貨，此功能僅限超商");
            }

            // 4. 呼叫綠界 Service
            // 回傳格式範例: "1|OK|12345678|F0000123" (成功) 或 "0|錯誤訊息" (失敗)
            String result = ecpayService.createLogisticsOrder(shipment);
            
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("系統錯誤: " + e.getMessage());
        }
    }
	@PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam Map<String, String> params) {
        // 綠界會以 application/x-www-form-urlencoded 格式 POST 過來
        String result = ecpayService.handleLogisticsCallback(params);
        return ResponseEntity.ok(result);
    }
}
