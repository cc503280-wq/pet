package com.pet.controller.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pet.model.member.Coupon;
import com.pet.model.member.CouponUsers;
import com.pet.model.member.Member;
import com.pet.model.order.CartItemDTO;
import com.pet.model.order.Order;
import com.pet.model.order.OrderItem;
import com.pet.model.order.Shipment;
import com.pet.model.product.Product;
import com.pet.service.member.CouponUsersService;
import com.pet.service.member.MemberService;
import com.pet.service.order.OrderItemService;
import com.pet.service.order.OrderService;
import com.pet.service.order.ShipmentService;
import com.pet.service.product.ProductService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/Cart")
public class CartController {

   
	@Autowired
	private ProductService pService;
	@Autowired
	private MemberService mService;
	@Autowired
	private CouponUsersService cService;
	@Autowired
	private OrderService oService;
	@Autowired
	private OrderItemService oiService;
	@Autowired
	private ShipmentService shipmentService;

   
@GetMapping("/shopping")
public String getProduct(Model m) {
	List<Product> products= pService.findAllProducts();
	m.addAttribute("products", products);
    return "shopping";
}

@PostMapping("/step1")
public String step1(
        @RequestParam("product") List<Integer> productIds,
        @RequestParam("qty") List<Integer> quantities,
        @RequestParam("productName") List<String> productName,
        @RequestParam("price") List<Integer> price,
        HttpSession session) {
	
	List<CartItemDTO> cart = new ArrayList<>();
    for (int i = 0; i < productIds.size(); i++) {
        cart.add(new CartItemDTO(productIds.get(i), quantities.get(i),productName.get(i),price.get(i)));
    }

    // 把商品 + 數量存 session
    session.setAttribute("cart",cart);
    
    
    List<Member> members = mService.getAllMembers();
    session.setAttribute("members", members);

    return "orderInsert";
}
@GetMapping("/byMember")
@ResponseBody
public List<CouponUsers> getCouponsByMember(@RequestParam Integer memberId){
	return cService.getCouponUsersByMemberId(memberId);
}

@PostMapping("/insertOrder")
public String insertOrder(
        @RequestParam("productId[]") List<Integer> productIds,
        @RequestParam("productName[]") List<String> productNames,
        @RequestParam("quantity[]") List<Integer> quantities,
        @RequestParam("price[]") List<Integer> prices,
        @RequestParam("member_id") Integer member_Id,
        @RequestParam("coupon_id") Integer coupon_Id,
        @RequestParam("total_price") BigDecimal total_price,
        @RequestParam("discountPrice") BigDecimal discountPrice,
        @RequestParam("method") String method,
        @RequestParam("fee") Integer fee,
        @RequestParam("finalAmount") Integer finalAmount,
        @RequestParam("recipientName") String recipientName,
        @RequestParam("recipientPhone") String recipientPhone,
        @RequestParam("shippingAddress") String shippingAddress
) {

    // 1. 建立訂單主表
    Order order = new Order();
    order.setMemberId(member_Id);
    order.setCouponId(coupon_Id);
    order.setOrderDate(LocalDateTime.now());
    order.setStatus("下訂單完成");
    order.setTotalAmountUndiscount(total_price);
    order.setTotalAmountDiscount(discountPrice);
    order.setTotalAmountDiscountPoints(BigDecimal.ZERO);
    order.setUsePoints(0);
    order.setGetPoints(0);
    Order orderId = oService.insertOrder(order);
    


    // 2. 建立訂單明細
    
    for (int i = 0; i < productIds.size(); i++) {
        OrderItem item = new OrderItem();
        item.setOrder(orderId);
        item.setProductId(productIds.get(i));
        item.setQuantity(quantities.get(i));
        item.setUnitPrice(prices.get(i));
        item.setSubtotal(quantities.get(i)*prices.get(i));
        oiService.insertOrderItem(item);
    }
   

    // 3. 建立物流表
    Shipment shipping = new Shipment();
    shipping.setOrder(orderId);
    shipping.setRecipientName(recipientName);
    shipping.setRecipientPhone(recipientPhone);
    shipping.setShippingAddress(shippingAddress);
    shipping.setShippingMethod(method);
    shipping.setShippingFee(fee);
    shipping.setStatus("未出貨");
    shipmentService.insertShipment(shipping);

    return "redirect:/orders/list";
}
}
