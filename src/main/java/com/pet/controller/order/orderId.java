package com.pet.controller.order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import com.pet.dao.order.OrderDao;
import com.pet.model.order.orderBean;

@WebServlet("/orderId")
public class orderId extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html; charset=UTF-8");
		OrderDao dao = new OrderDao();
		String memberId = request.getParameter("memberId");
        List<orderBean> list = dao.findIdOrders(Integer.parseInt(memberId)); // 查詢會員ID所屬的訂單

        request.setAttribute("orderList", list);
        request.getRequestDispatcher("/admin/order/orderList.jsp").forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
