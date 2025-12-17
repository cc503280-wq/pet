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
import com.pet.model.member.Member;

@WebServlet("/memberServletOrder")
public class memberServletOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json; charset=UTF-8");
		List<Member> list = new ArrayList<>();
		
		ResultSet rs = null;
 		PreparedStatement stmt = null;
 		Connection conn =null;
		 
 		try {
			Context context = new InitialContext();
			DataSource ds = (DataSource)context.lookup("java:comp/env/jdbc/petDB");
			conn = ds.getConnection();
			String sql = "SELECT member_id,name FROM members";
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery(); 
			while (rs.next()) {
                int id = rs.getInt("member_id");
                String name = rs.getString("name");
                Member m = new Member(id, name);
                list.add(m);
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
