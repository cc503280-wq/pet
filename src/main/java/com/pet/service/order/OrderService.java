package com.pet.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.aspect.LogAction;
import com.pet.config.CloudinaryConfig;
import com.pet.dao.member.CouponRepository;
import com.pet.dao.member.CouponUsersRealRepository;
import com.pet.dao.order.OrderItemRepository;
import com.pet.dao.order.OrderRepository;
import com.pet.dao.order.ShipmentRepository;
import com.pet.dao.product.CartItemRepository;
import com.pet.dto.order.OrderCheckOutDTO;
import com.pet.model.member.Coupon;
import com.pet.model.order.Order;
import com.pet.model.order.OrderItem;
import com.pet.model.order.Shipment;
import com.pet.model.product.CartItem;
import com.pet.service.member.CouponUsersRealService;
import com.pet.service.member.MemberService;
import com.pet.service.product.CartItemService;
import com.pet.service.product.ProductService;

@Service
public class OrderService {

    private final CouponUsersRealService couponUsersRealService;

    private final CloudinaryConfig cloudinaryConfig;
	@Autowired
	private OrderRepository oRepository;

	
	@Autowired 
	private OrderItemRepository oiRepository;
	@Autowired
	private ShipmentRepository sRepository;
	@Autowired
	private CartItemRepository cRepository;
	@Autowired
	private CouponRepository couponRepository;
	
	@Autowired
	private CouponUsersRealRepository couponUsersRealRepository;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private ProductService pService;
	
	@Autowired
	private CartItemService cService;
	
	@Autowired
    private OrderItemService oiService;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private CouponUsersRealService curService;
    @Autowired
    private MemberService mService;
	

    OrderService(CloudinaryConfig cloudinaryConfig, CouponUsersRealService couponUsersRealService) {
        this.cloudinaryConfig = cloudinaryConfig;
        this.couponUsersRealService = couponUsersRealService;
    }
	
	//找全部訂單
	public List<Order> getAllOrders(){
		return oRepository.findAll();
	}
	
	
	//找單筆訂單透過訂單編號
	public Order getOrderById(Integer id) {
		return oRepository.getById(id);
	}
	
	public List<Order> getUserOrder(Integer id){
		
		return oRepository.findByMemberId(id);
	}
	
	//找多筆透過會員編號
	 public List<Order> getOrderByMemberId(Integer memberId) {
	        if (memberId != null) {
	            return oRepository.findByMemberId(memberId);
	        }
	        return oRepository.findAll();
	        }
	
	public Order insertOrder(Order order) {
		return oRepository.save(order);
	}
	
	public String updateOrder(Order order) {
		oRepository.save(order);
		return "update OK";
	}
	
	@Transactional
    public boolean updateOrderStatus(Integer orderId, String status) {
        return oRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(status);  // Lombok 自動生成 setter
                    return true;
                })
                .orElse(false);
    }
	
	@Transactional
	@LogAction(type = LogAction.ActionType.CREATE_ORDER) 
	public Order userOrder(OrderCheckOutDTO orderCheckOutDTO) {
		
		List<CartItem> cartItems = cRepository.findByMember_MemberId(orderCheckOutDTO.getMemberId());
		if (cartItems == null || cartItems.isEmpty()) {
	        throw new RuntimeException("購物車為空，無法建立訂單");
	    }
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		
		Integer shippingFee = 0; 
		//計算物流費用
        if ("宅配".equals(orderCheckOutDTO.getShippingMethod())) {
            shippingFee = 100; // 範例：宅配運費 100
        }
        //1.資料庫取得購物車資料
		for (CartItem cartItem : cartItems) {
		    BigDecimal itemTotal =
		    		BigDecimal.valueOf(cartItem.getProduct().getPrice()).multiply(BigDecimal.valueOf(cartItem.getQuantity()));
		            //cartItem.getPriceAtAdded()
		                    //.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

		    totalAmount = totalAmount.add(itemTotal);
		}
		BigDecimal amountAfterDiscount = totalAmount;

        if (orderCheckOutDTO.getCouponId() != null) {

        	Coupon coupon = couponRepository.findById(orderCheckOutDTO.getCouponId())
                    .orElseThrow(() -> new RuntimeException("無效的優惠券 ID"));
            BigDecimal discountValue =
                    BigDecimal.valueOf(coupon.getDiscountValue());

            if ("amount".equals(coupon.getDiscountType())) {
                // 固定金額折抵（例如折 100 元）
                amountAfterDiscount =
                        totalAmount.subtract(discountValue);

            } else if ("rate".equals(coupon.getDiscountType())) {
                // 比例折扣（例如 0.9 = 打 9 折）
                amountAfterDiscount =
                        totalAmount.multiply(discountValue);
            }
            // 防止折到負數
            if (amountAfterDiscount.compareTo(BigDecimal.ZERO) < 0) {
                amountAfterDiscount = BigDecimal.ZERO;
            }
            amountAfterDiscount = amountAfterDiscount.add(BigDecimal.valueOf(shippingFee));
        }else {
        	amountAfterDiscount = amountAfterDiscount.add(BigDecimal.valueOf(shippingFee));
		}
        BigDecimal totalAmountDiscountPoints = amountAfterDiscount;
        if (orderCheckOutDTO.getPointsUsed() != null && orderCheckOutDTO.getPointsUsed() > 0) {
            BigDecimal usedPoint = BigDecimal.valueOf(orderCheckOutDTO.getPointsUsed());
            
            // 扣除點數
            totalAmountDiscountPoints = totalAmountDiscountPoints.subtract(usedPoint);
            
            // 防止點數折抵後變成負數 (例如應付 100，卻用了 200 點)
            if (totalAmountDiscountPoints.compareTo(BigDecimal.ZERO) < 0) {
                // 視業務邏輯，這裡可以拋出錯誤，或是自動歸零
                // throw new RuntimeException("折抵點數超過應付金額"); 
                totalAmountDiscountPoints = BigDecimal.ZERO; 
            }
        }
        //計算滿百贈點(無條件捨去)
        Integer getPoints = totalAmountDiscountPoints.divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.FLOOR).intValue();
        //2.建立並儲存訂單主表
        Order order = Order.builder()
                .memberId(orderCheckOutDTO.getMemberId())
                .orderDate(LocalDateTime.now())
                .status("處理中") // 訂單初始狀態
                .totalAmountUndiscount(totalAmount)
                .couponId(orderCheckOutDTO.getCouponId())
                .totalAmountDiscount(amountAfterDiscount) 
                .usePoints(orderCheckOutDTO.getPointsUsed())
                .totalAmountDiscountPoints(totalAmountDiscountPoints)
                .getPoints(getPoints)
                .build();
        // 先 save 才能拿到 orderId
        Order savedOrder = oRepository.save(order);
        
     // 3. 建立並儲存 Shipment (從表)
        // 根據前端傳來的 shippingMethod 決定運費 (這裡範例設為 0)
        

        Shipment shipment = Shipment.builder()
                .order(savedOrder) // 關聯剛剛儲存的訂單
                .recipientName(orderCheckOutDTO.getReceiverName())
                .recipientPhone(orderCheckOutDTO.getReceiverPhone())
                .shippingAddress(orderCheckOutDTO.getReceiverAddress())
                .shippingMethod(orderCheckOutDTO.getShippingMethod())
                .shippingFee(shippingFee)
                .status("備貨中") // 物流初始狀態
                .build();

        sRepository.save(shipment);
        
        //4.建立訂單明細
        for (CartItem cartItem : cartItems) {
        	BigDecimal itemTotal =
		    		BigDecimal.valueOf(cartItem.getProduct().getPrice()).multiply(BigDecimal.valueOf(cartItem.getQuantity()));
//		            cartItem.getPriceAtAdded()
//		                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
			OrderItem orderItem =new OrderItem();
			orderItem.setOrder(savedOrder);
			orderItem.setProductId(cartItem.getProduct().getProductId());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setSubtotal(itemTotal);
//			orderItem.setUnitPrice(cartItem.getPriceAtAdded());
			orderItem.setUnitPrice(BigDecimal.valueOf(cartItem.getProduct().getPrice()));
			oiRepository.save(orderItem);
			//修改商品庫存
			pService.updateProductStock(cartItem.getProduct().getProductId(), 0, cartItem.getQuantity());
		}

        // 5.修改優惠券狀態
     		if (orderCheckOutDTO.getCouponUsersId()!= null) {
     			couponUsersRealRepository.UpdateCouponUsersReal(orderCheckOutDTO.getCouponUsersId(), "used", LocalDate.now());
     		}

     	// 6.修改會員幣數量

     		memberService.updateMemberPoints(orderCheckOutDTO.getMemberId(),orderCheckOutDTO.getPointsUsed(), 0);

     	//7.清空購物車
     		
     		cService.clearCart(orderCheckOutDTO.getMemberId());
      
        return savedOrder;
        
        
	}
	public String generateOrdersCsv(Integer memberId) {
        List<Order> orders;
        if (memberId != null) {
            orders = oRepository.findByMemberId(memberId);
        } else {
            orders = oRepository.findAll();
        }

        StringBuilder sb = new StringBuilder();
        // CSV 標頭
        sb.append("訂單編號,會員編號,成立日期,當前狀態,商品總價,優惠券,券後總價,使用點數,點數折價後價錢,得到點數\n");

        for (Order o : orders) {
            sb.append(String.format("%d,%d,%s,%s,%.2f,%s,%.2f,%d,%.2f,%d\n",
                    o.getOrderId(),
                    o.getMemberId(),
                    o.getOrderDate(),
                    o.getStatus(),
                    o.getTotalAmountUndiscount(),
                    o.getCouponId() != null ? o.getCouponId() : "",
                    o.getTotalAmountDiscount(),
                    o.getUsePoints(),
                    o.getTotalAmountDiscountPoints(),
                    o.getGetPoints()));
        }

        return sb.toString();
    }
	
	@Transactional
    public Order processAdminOrder(
            List<Integer> productIds, List<Integer> quantities, List<Integer> prices,
            Integer memberId, Integer couponId, Integer couponUserId,
            BigDecimal totalPrice, BigDecimal discountPrice, BigDecimal finalAmount,
            BigDecimal totalAmountDiscountPoints, Integer usedPoint, Integer getPoint,
            String method, Integer fee, String recipientName, String recipientPhone, String shippingAddress) {

        // 1. 建立並儲存訂單主表
        Order order = new Order();
        order.setMemberId(memberId);
        order.setCouponId(couponUserId != null ? couponId : null);
        order.setOrderDate(LocalDateTime.now().withNano(0));
        order.setStatus("付款完成");
        order.setTotalAmountUndiscount(totalPrice);
        order.setTotalAmountDiscount(discountPrice);
        order.setTotalAmountDiscountPoints(totalAmountDiscountPoints);
        order.setUsePoints(usedPoint);
        order.setGetPoints(getPoint);
        
        Order savedOrder = oRepository.save(order);

        // 2. 建立並儲存訂單明細
        for (int i = 0; i < productIds.size(); i++) {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProductId(productIds.get(i));
            item.setQuantity(quantities.get(i));
            item.setUnitPrice(BigDecimal.valueOf(prices.get(i)));
            
            // 計算小計
            BigDecimal subtotal = BigDecimal.valueOf(quantities.get(i))
                                            .multiply(BigDecimal.valueOf(prices.get(i)));
            item.setSubtotal(subtotal);
            
            oiService.insertOrderItem(item);
        }

        // 3. 建立並儲存物流表
        Shipment shipping = new Shipment();
        shipping.setOrder(savedOrder);
        shipping.setRecipientName(recipientName);
        shipping.setRecipientPhone(recipientPhone);
        shipping.setShippingAddress(shippingAddress);
        shipping.setShippingMethod(method);
        shipping.setShippingFee(fee);
        shipping.setStatus("未出貨");
        shipmentService.insertShipment(shipping);

        // 4. 修改優惠券狀態
        if (couponUserId != null && couponUserId != 0) {
            curService.CouponUsersUpdate(couponUserId, "used", LocalDate.now());
        }

        // 5. 修改會員幣數量
        mService.updateMemberPoints(memberId, usedPoint, 0);

        return savedOrder;
    }
}

