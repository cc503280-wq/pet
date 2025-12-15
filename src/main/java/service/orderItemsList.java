package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bean.orderItemBean;
import dao.OrderItemsDao;

import java.io.IOException;
import java.util.List;

@WebServlet("/orderItemsList")
public class orderItemsList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html; charset=UTF-8");
		OrderItemsDao dao = new OrderItemsDao();
        List<orderItemBean> list = dao.findAllOrderItems(); // 查詢會員ID所屬的訂單

        request.setAttribute("orderItemsList", list);
        request.getRequestDispatcher("admin/order/orderItemsList.jsp").forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
