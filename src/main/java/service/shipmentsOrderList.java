package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bean.shipmentsBean;
import dao.ShipmentsDao;

import java.io.IOException;
import java.util.List;

@WebServlet("/shipmentsOrderList")
public class shipmentsOrderList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html; charset=UTF-8");
		ShipmentsDao dao = new ShipmentsDao();
		String orderId = request.getParameter("orderId");
        List<shipmentsBean> list = dao.findOrderShipments(Integer.parseInt(orderId)); // 查詢全部訂單

        request.setAttribute("shipmentsList", list);
        request.getRequestDispatcher("admin/order/shipmentsList.jsp").forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
