package com.pet.controller.order;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pet.model.order.OrderItem;
import com.pet.model.order.Shipment;
import com.pet.service.order.OrderService;
import com.pet.service.order.ShipmentService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/shipment")
public class ShipmentController {


	@Autowired
	private ShipmentService shipmentService;


	
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
}
