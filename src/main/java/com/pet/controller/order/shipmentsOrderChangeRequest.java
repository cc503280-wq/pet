package com.pet.controller.order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import com.pet.dao.order.ShipmentsDao;
import com.pet.model.order.shipmentsBean;

@WebServlet("/shipmentsOrderChangeRequest")
public class shipmentsOrderChangeRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html; charset=UTF-8");
	    String shipmentIdStr = request.getParameter("shipmentId");
        if (shipmentIdStr == null || shipmentIdStr.trim().isEmpty()) {
            response.getWriter().println("缺少 shipmentId");
            return;
        }

        int shipmentId = Integer.parseInt(shipmentIdStr);

        ShipmentsDao dao = new ShipmentsDao();
        shipmentsBean shipment = dao.findShipmentById(shipmentId); // Hibernate 版本
        if (shipment == null) {
            response.getWriter().println("找不到出貨資料");
            return;
        }

        // 包成 List 給 JSP
        List<shipmentsBean> list = List.of(shipment);

        request.setAttribute("shipmentsList", list);
        request.getRequestDispatcher("admin/order/updateShipment.jsp").forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
