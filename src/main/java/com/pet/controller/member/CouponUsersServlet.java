package com.pet.controller.member;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tomcat.jakartaee.commons.lang3.ObjectUtils.Null;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pet.dao.member.CouponUsersDao;
import com.pet.model.member.CouponUsers;
import com.pet.model.member.Member;


@WebServlet("/CouponUsersServlet")
@MultipartConfig
public class CouponUsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	CouponUsersDao couponUsersDao = new CouponUsersDao(); 
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		String action = request.getParameter("action");
		System.out.println(action);
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
		PrintWriter out = response.getWriter();
		
		try { 
			
			switch(action) {
			//查詢全部
			case "list":

			    List<CouponUsers> couponUsers = couponUsersDao.queryAllCouponUsers();
                out.print(gson.toJson(couponUsers));
                break;

				//依couponId查詢
			case "queryByCouponId":
                int couponId = Integer.parseInt(request.getParameter("couponId"));
                List<CouponUsers> couponUsers1 = couponUsersDao.queryCouponUsersByCouponId(couponId);
                out.print(gson.toJson(couponUsers1));
                break;
				
				//依 memberId 查詢
			case "queryByMemberId":
                int memberId = Integer.parseInt(request.getParameter("memberId"));
                List<CouponUsers> couponUsers2 = couponUsersDao.queryCouponUsersByMemberId(memberId);
                out.print(gson.toJson(couponUsers2));
                break;
				
				//條件查詢
                
			default:
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.print("{\"error\":\"未知的 action\"}");
				break;
			}
			
		} catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"伺服器錯誤\"}");
        }
		out.flush();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
