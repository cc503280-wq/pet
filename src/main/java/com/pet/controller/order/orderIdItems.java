package com.pet.controller.order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import com.pet.dao.order.OrderDao;
import com.pet.dao.order.OrderItemsDao;
import com.pet.model.order.orderBean;
import com.pet.model.order.orderItemBean;

@WebServlet("/orderIdItems")
public class orderIdItems extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html; charset=UTF-8");
	    String orderIdStr = request.getParameter("orderId");
        if(orderIdStr == null || orderIdStr.trim().isEmpty()) {
            response.getWriter().println("缺少 orderId");
            return;
        }

        int orderId = Integer.parseInt(orderIdStr);

        // 新增：用 Hibernate 拿到 orderBean
        OrderDao orderDao= new OrderDao();
        orderBean order = orderDao.findOrderById(orderId);
        if(order == null) {
            response.getWriter().println("找不到訂單");
            return;
        }

        // Hibernate 查詢訂單明細
        OrderItemsDao dao = new OrderItemsDao();
        List<orderItemBean> list = dao.findOrderItems(order); // 這裡改成傳 orderBean

        request.setAttribute("orderItemsList", list);
        request.getRequestDispatcher("admin/order/orderItemsList.jsp").forward(request, response);
    }

	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
