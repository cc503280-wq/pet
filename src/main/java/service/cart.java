package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bean.orderItemBean;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet("/cart")
public class cart extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String[] productIds = request.getParameterValues("product[]");
	    String[] qtys = request.getParameterValues("qty[]");
		
	    if (productIds == null || qtys == null) {
	        response.getWriter().write("沒有收到任何商品資料");
	        return;
	    }
	    
	    List<orderItemBean> itemList = new ArrayList<>();
	    
		ResultSet rs = null;
 		PreparedStatement stmt = null;
 		Connection conn =null;
		try {
			Context context = new InitialContext();
			DataSource ds = (DataSource)context.lookup("java:comp/env/jdbc/petDB");
			conn = ds.getConnection();
			String sql = "select product_id,product_name,price from products where product_id=?";
			stmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < productIds.length; i++) {
				 String pid = productIds[i];
		         Integer qty = Integer.parseInt(qtys[i]);
		         stmt.setString(1, pid);
		         rs = stmt.executeQuery();
		         
		         if(rs.next()) {
		        	 	orderItemBean item = new orderItemBean();
						
		        	 	int productId = rs.getInt("product_id");
		        	 	String productName=rs.getString("product_name");
		                double unitPrice = rs.getDouble("price");
		                
		                item.setProductId(productId);
		                item.setQuantity(qty);
		                item.setProductName(productName);
		                item.setUnitPrice(unitPrice);
		                item.setSubtotal(unitPrice*qty);
		                itemList.add(item);
		                rs.close();
					}
		         
			}
			
			
			
			
			request.setAttribute("items", itemList);
			
			
			request.getRequestDispatcher("/admin/order/order.jsp")
			.forward(request, response);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (rs != null) {
	            try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
	        }
	        // 再關 PreparedStatement
	        if (stmt != null) {
	            try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
	        }
	        // 最後關 Connection
	        if (conn != null) {
	            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
	        }
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
