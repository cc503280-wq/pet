package com.pet.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
import com.pet.dao.product.ProductRepository;
import com.pet.dto.order.OrderCheckOutDTO;
import com.pet.model.member.Coupon;
import com.pet.model.order.Order;
import com.pet.model.order.OrderItem;
import com.pet.model.order.Shipment;
import com.pet.model.product.CartItem;
import com.pet.model.product.Product;
import com.pet.service.member.CouponUsersRealService;
import com.pet.service.member.MemberService;
import com.pet.service.product.CartItemService;
import com.pet.service.product.ProductService;

@Service
public class OrderService {

	e final CouponUsersRealService couponUsersRealService;

	e final CloudinaryConfig cloudinaryConfig;
	@Autowired
	private OrderRepository oRepository;

	private Or
	@Autowired

		@Autowire
	private CouponRepository couponRepository;

	Autowired
	private CouponUsersRealRepository couponUs

	Autowired
	private MemberService memberService;

	Autowired
	private ProductService pService;

	Autowired
	private CartItemService cService

	rderServic
	his.cloudinaryConfig = cloudinar

	
		
		
	c

		 
	 

		

	 
		

	

		r eturn oRepository.findByMemberId(id); 

		
	

	pu blic Order insertOrder(Order order) {
		return oRepository.save(order);
	}


		oRepository.save(order);
		return "update OK";
	}

		ansactional
	p

	.

	r
	
		false);
				
					 
					
				 =
				
	

		t<CartItem> cartItems = cRepository.
				ms == null || c
					eException("購物車為空，無法建立訂單 ;

				
				Amount = BigDec
			
		

		

			shippingFe
		 
		 // 範例：宅配運費 100
			
		料
		ar tItem cartItem : cartItems) {
			BigDecimal itemTotal = BigDecimal.va
			multiply(BigDecimal.va rtItem.getPriceAtAdded()
					
			y( BigDecimal.valueOf(cartIte
			 

			
		BigDecimal amountAfterDiscount = totalAmount;


		

			lseThrow(() -> new RuntimeException("無效的優惠券 ID"));
					ntValue = BigDecimal.valueOf(coupon.getDiscountValue()
			 ls(coupon.getDiscountType())) {

			terDiscount = totalAmount.subtract(discountValue
				
				ate".equals(coupon.ge  折）

			
				
				erDiscount.compareTo( = BigDecimal.ZERO;
			
			erDiscoun
			
				scount = amountAfterDiscount.add(BigDe
			
			 totalAmountDiscountPoints = amountAfterDiscount;
		d erChec
			imal usedPoint = BigDecimal.valueOf(orderCheckOutDTO.getPointsUsed());

		點數
		AmountDiscountPoints = totalAmountDiscountPoints.subtract(usedPoint);
			

			AmountD
			，這裡可以拋出錯誤，或是自動歸零

			untDiscountPoints = BigDecimal.ZERO
			
				
				去)
				nts = totalAmountDiscountPoints.divide(BigDe
			e
		立
		or der = Order.b
		berId(orderCheckOutDTO.getMemberId())
				
		er Date(LocalD
		tus("處理中") // 訂單初始狀態
				Undiscount(totalAmount)
				derCheckOutDTO.getCouponId())
				Discount(amountAfterDisc
				rderCheckOutDTO.getPointsUsed())
				DiscountPoints(totalAmountDiscountPoints)
				etPoints)
				
				 orderId
				er = oRepository.save
				
		建立並儲存 Shipment (從表)
		端傳來的 shippingMethod 決定運費 (這裡範例設為 0)

		pment shipment = Shipment
		er(savedO.recipientNa rd erCheckOutDTO

		ppingFee(shippingFee).status("備貨中.buil
				
				e(shipment);
				
				
				
				 = BigDecimal.valueOf(car
				igDecimal.valueOf(cartIt
				etPriceAt

		Item.setOrder(savedOrder);

		It em.setQu
		Item.setSubtotal(itemTotal);
			erItem.setUnitPrice(ca Item.setUnitPrice(BigDecimal.valueOf(cartItem.getPro
					duct().getPrice()));
					 save(orderItem);
					 
			pService.updateProduc tStock(cartItem.getProduct().getProductId(), 0, cartItem.getQuantity());
		}

		// 5.修改優惠券狀態
		if (orderCheckOutDTO.getCouponUser
					 uponUsersRealRepository.UpdateCouponUsersReal(orderCheckOutDTO.getCouponUsersId(), "used",
					LocalDate.now());
		}
 
		// 6.修改會員幣數量


		
		 
			空購物車
					
		

		

		 

		 

		

		

	

