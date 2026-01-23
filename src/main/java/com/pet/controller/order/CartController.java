package com.pet.controller.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pet.dao.member.CouponUsersRealRepository;
import com.pet.model.member.Coupon;
import com.pet.model.member.CouponUsers;
import com.pet.model.member.Member;
import com.pet.model.order.CartItemDTO;
import com.pet.model.order.Order;
import com.pet.model.order.OrderItem;
import com.pet.model.order.Shipment;
import com.pet.model.product.Product;
import com.pet.service.member.CouponUsersRealService;
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
	@Autowired
	private CouponUsersRealService curService;

   
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
public List<CouponUsers> getCouponsByMember(@RequestParam Integer memberId,@RequestParam BigDecimal totalPrice){
	return cService.getOrderCouponUsers(memberId, totalPrice);
}

@PostMapping("/insertOrder")
public String insertOrder(
        @RequestParam("productId[]") List<Integer> productIds,
        @RequestParam("productName[]") List<String> productNames,
        @RequestParam("quantity[]") List<Integer> quantities,
        @RequestParam("price[]") List<Integer> prices,
        @RequestParam("member_id") Integer member_Id,
        @RequestParam(required = false,name = "coupon_id") Integer coupon_Id,
        @RequestParam("total_price") BigDecimal total_price,
        @RequestParam("discountPrice") BigDecimal discountPrice,
        @RequestParam("method") String method,
        @RequestParam("fee") Integer fee,
        @RequestParam("finalAmount") BigDecimal finalAmount,
        @RequestParam("recipientName") String recipientName,
        @RequestParam("recipientPhone") String recipientPhone,
        @RequestParam("shippingAddress") String shippingAddress,
        @RequestParam(required = false,name = "couponUserId") Integer couponUserId,
        @RequestParam("usedPoint") Integer usedPoint,
        @RequestParam("totalAmountDiscountPoints") BigDecimal TotalAmountDiscountPoints,
        @RequestParam("getPoint") Integer getPoint
		) {
	System.out.println(coupon_Id+"user:"+couponUserId);
    // 1. 建立訂單主表
    Order order = new Order();
    order.setMemberId(member_Id);
    if (couponUserId != null) {
        order.setCouponId(coupon_Id);
    } else {
        order.setCouponId(null); 
    }
    order.setOrderDate(LocalDateTime.now().withNano(0));
    order.setStatus("下訂單完成");
    order.setTotalAmountUndiscount(total_price);
    order.setTotalAmountDiscount(discountPrice);
    order.setTotalAmountDiscountPoints(TotalAmountDiscountPoints);
    order.setUsePoints(usedPoint);
    order.setGetPoints(getPoint);
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
    //4.修改優惠券狀態
    if (couponUserId != null) {
    	curService.CouponUsersUpdate(couponUserId, "used", LocalDate.now());
	}
    
    
    //5.修改會員幣數量
    
    
    mService.updateMemberPoints(member_Id, usedPoint, getPoint);

    return "redirect:/orders/list";
}
}
