package com.pet.controller.order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import com.pet.dao.member.CouponUsersDao;
import com.pet.dao.order.OrderDao;
import com.pet.dao.order.OrderItemsDao;
import com.pet.dao.order.ShipmentsDao;

@WebServlet("/insertOrder")
public class insertOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		try {
			// 1. 從表單抓取資料
			int memberId = Integer.parseInt(request.getParameter("member_id"));
			String status = "已建立訂單";

			BigDecimal totalAmountUndiscount = new BigDecimal(request.getParameter("total_price"));

			String couponIdStr = request.getParameter("coupon_id");
			Integer couponId = null;
			if (couponIdStr != null && !couponIdStr.trim().isEmpty() && !couponIdStr.equals("0")) {
				couponId = Integer.parseInt(couponIdStr);
			}

			BigDecimal totalAmountDiscount = new BigDecimal(request.getParameter("finalAmount"));
			int usePoints = 0;
			BigDecimal totalAmountDiscountPoints = BigDecimal.ZERO;
			int getPoints = 0;

			Date orderDate = new Date(); // 可改成表單傳入或系統時間

			// 2. 呼叫 DAO 插入
			OrderDao orderDao = new OrderDao();
			int orderId = orderDao.insertOrder(memberId, orderDate, status, totalAmountUndiscount, couponId,
					totalAmountDiscount, usePoints, totalAmountDiscountPoints, getPoints);

			// 3. 判斷是否成功
			if (orderId != -1) {
				response.getWriter().println("訂單插入成功，order_id = " + orderId);
			} else {
				response.getWriter().println("訂單插入失敗");
			}

			CouponUsersDao couponUsersDao = new CouponUsersDao();
			couponUsersDao.usedcoupon( couponId);
			String[] productIdsStr = request.getParameterValues("productId[]");
			String[] quantitiesStr = request.getParameterValues("quantity[]");
			String[] pricesStr = request.getParameterValues("price[]");

			if (productIdsStr == null || quantitiesStr == null || pricesStr == null) {
				response.getWriter().println("沒有商品明細，請先加入商品！");
				return;
			}

			int[] productIds = new int[productIdsStr.length];
			int[] quantities = new int[quantitiesStr.length];
			BigDecimal[] unitPrices = new BigDecimal[pricesStr.length];
			BigDecimal[] subtotal = new BigDecimal[productIdsStr.length];

			for (int i = 0; i < productIdsStr.length; i++) {
				productIds[i] = Integer.parseInt(productIdsStr[i]);
				quantities[i] = Integer.parseInt(quantitiesStr[i]);
				unitPrices[i] = new BigDecimal(pricesStr[i]);
				subtotal[i] = unitPrices[i].multiply(BigDecimal.valueOf(quantities[i]));

			}

			OrderItemsDao itemsDao = new OrderItemsDao();

			for (int i = 0; i < productIds.length; i++) {
				boolean success = itemsDao.insertOrderItem(productIds[i], orderId, quantities[i], unitPrices[i],
						subtotal[i]);
				if (!success) {
					System.out.println("商品明細插入失敗: productId=" + productIds[i]);
				}
			}

			String method = request.getParameter("method");
			Integer fee = Integer.parseInt(request.getParameter("fee"));
			String recipientName = request.getParameter("recipientName");
			String recipientPhone = request.getParameter("recipientPhone");
			String shippingAddress = request.getParameter("shippingAddress");

			ShipmentsDao shipmentsDao = new ShipmentsDao();
			shipmentsDao.insertShipment(orderId, method, fee, recipientName, recipientPhone, shippingAddress);

		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().println("發生錯誤：" + e.getMessage());
		}
		request.getRequestDispatcher("/orderList").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
