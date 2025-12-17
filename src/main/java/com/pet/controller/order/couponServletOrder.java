package com.pet.controller.order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

import com.google.gson.Gson;
import com.pet.model.member.Coupon;

@WebServlet("/couponServletOrder")
public class couponServletOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json; charset=UTF-8");
		String memberIdParam = request.getParameter("memberId");
		int memberId = Integer.parseInt(memberIdParam);
		List<Coupon> list = new ArrayList<>();
		
		ResultSet rs = null;
 		PreparedStatement stmt = null;
 		Connection conn =null;
		 
 		try {
			Context context = new InitialContext();
			DataSource ds = (DataSource)context.lookup("java:comp/env/jdbc/petDB");
			conn = ds.getConnection();
			String sql = 
		            "SELECT * FROM vw_member_coupons " +
		            "WHERE member_id = ? " +
		            "  AND user_status = 'unused' " +
		            "  AND use_start_at <= GETDATE() " +
		            "  AND use_end_at >= GETDATE()";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, memberId);
			rs = stmt.executeQuery(); 
			while (rs.next()) {
                Coupon c = new Coupon();

                c.setCouponId(rs.getInt("coupon_id"));
                c.setCode(rs.getString("code"));
                c.setDiscountType(rs.getString("discount_type"));
                c.setDiscountValue(rs.getDouble("discount_value"));
                c.setMinPurchase(rs.getInt("min_purchase"));
                c.setUseStartAt(rs.getDate("use_start_at"));
                c.setUseEndAt(rs.getDate("use_end_at"));
                c.setStatus(rs.getString("coupon_status"));

                list.add(c);
            }
			
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
 		Gson gson = new Gson();
        String json = gson.toJson(list);

        response.getWriter().write(json);

	}
		


	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
