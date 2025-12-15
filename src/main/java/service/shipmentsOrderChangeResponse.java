package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.ShipmentsDao;

import java.io.IOException;
import java.text.ParseException;


@WebServlet("/shipmentsOrderChangeResponse")
public class shipmentsOrderChangeResponse extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html; charset=UTF-8");
		ShipmentsDao dao = new ShipmentsDao();
		Integer shipmentId = Integer.parseInt(request.getParameter("shipmentId"));
		String trackingNumber =request.getParameter("trackingNumber");
		String shippedAtStr =request.getParameter("shippedAt");
		String deliveredAtStr =request.getParameter("deliveredAt");
		String status = request.getParameter("status");
		java.util.Date deliveredAt = null;
		java.util.Date shippedAt = null;
		
		try {
			if (shippedAtStr != null && !shippedAtStr.isEmpty()) {
	            shippedAt = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(shippedAtStr);
	        }
			if (deliveredAtStr != null && !deliveredAtStr.isEmpty()) {
	            deliveredAt = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(deliveredAtStr);
	        }
			
		}catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
       
		dao.changeShipmentId(trackingNumber, shippedAt, deliveredAt, status, shipmentId);
		
       
        request.getRequestDispatcher("/shipmentsList").forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
