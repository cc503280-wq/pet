package com.pet.service.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dao.order.OrderItemRepository;
import com.pet.dto.order.OrderItemDTO;
import com.pet.model.order.OrderItem;
import com.pet.model.order.Shipment;
import com.pet.model.product.Product;
import com.pet.service.product.ProductService;


@Service
public class OrderItemService {
	@Autowired
	private OrderItemRepository oiRepository;
	@Autowired
	private ProductService pService;
	@Autowired
	private ShipmentService shipmentService;

	// 找全部訂單明細
	public List<OrderItem> getAllOrderItem() {
		List<OrderItem> orderItems =new ArrayList<OrderItem>();
		
		for (OrderItem orderItem : oiRepository.findAll()) {
			orderItem.setProductName(pService.getProductById(orderItem.getProductId()).getProductName());
			orderItems.add(orderItem);
		}
		return orderItems;
	}

	// 根據訂單明細編號搜尋
	public OrderItem getOrderItem(Integer id) {
		return oiRepository.findById(id).orElseThrow();
	}
	//根據商品編號進行查詢
	public List<OrderItem> getOrderItemByProductId(Integer productId) {
		List<OrderItem> list = oiRepository.findByProductId(productId);
		for (OrderItem item : list) {
	        Product p = pService.getProductById(item.getProductId());
	        if (p != null) {
	            item.setProductName(p.getProductName());
	        }
	    }
		return oiRepository.findByProductId(productId);
	}

	//根據訂單編號查詢明細
	public List<OrderItem> getOrderItemByorder(Integer id) {
		List<OrderItem> orderItems =new ArrayList<OrderItem>();
		
		for (OrderItem orderItem : oiRepository.findByOrder_OrderId(id)) {
			orderItem.setProductName(pService.getProductById(orderItem.getProductId()).getProductName());
			orderItems.add(orderItem);
		}
		return orderItems;
	}
	public OrderItem insertOrderItem(OrderItem orderItem) {
		return oiRepository.save(orderItem);
	}
	
	//將訂單細項與商品資料合成一個DTO回傳給前端介面
	public List<OrderItemDTO> getOrderItemDTO(Integer orderId) {
		List<OrderItem> orderItems = oiRepository.findByOrder_OrderId(orderId);
		List<OrderItemDTO> list = new ArrayList<OrderItemDTO>();
		
		for (OrderItem orderItem : orderItems) {
			OrderItemDTO orderItemDTO = new OrderItemDTO();
			Product product = pService.getProductById(orderItem.getProductId());
			List<Shipment> shipment =shipmentService.getShipmentByOrderId(orderId);
			orderItemDTO.setOrderItem(orderItem);
			orderItemDTO.setProduct(product);
			orderItemDTO.setShipment(shipment.get(0));
			list.add(orderItemDTO);
		}
		return list;
	}
	//輸出訂單明細表單(csv)
	public String generateCsv(Integer orderId) {
		List<OrderItem> list;
		
        if (orderId != null) {
            list = getOrderItemByorder(orderId);
        } else {
            list = getAllOrderItem();
        }
        StringBuilder sb = new StringBuilder();
		// CSV Header
        sb.append("明細編號,訂單編號,商品編號,商品名稱,商品數量,商品單價,小計\n");
        
        for (OrderItem orderItem : list) {
        	BigDecimal subtotal =
                    orderItem.getUnitPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity()));
        	
        	sb.append(orderItem.getProductItemId()).append(",")
            .append(orderItem.getOrder().getOrderId()).append(",")
            .append(orderItem.getProductId()).append(",")
            .append("\"").append(orderItem.getProductName()).append("\"").append(",")
            .append(orderItem.getQuantity()).append(",")
            .append(orderItem.getUnitPrice()).append(",")
            .append(subtotal)
            .append("\n");
		}
        return sb.toString();
		
	}

}
