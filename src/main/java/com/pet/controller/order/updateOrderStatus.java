package com.pet.controller.order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import com.pet.dao.order.OrderDao;

@WebServlet("/updateOrderStatus")
public class updateOrderStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html; charset=UTF-8");
	    
		OrderDao dao = new OrderDao();
		
		
        Integer orderId=Integer.parseInt(request.getParameter("orderId"));
        String status =request.getParameter("status");
        dao.changeOrder(status, orderId);
        
        response.getWriter().write("OK");
       
       

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
