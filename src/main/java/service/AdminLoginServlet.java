package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.google.gson.Gson;

import bean.Admin;
import dao.AdminDao;


@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		String Email = request.getParameter("email");
		String password = request.getParameter("password");
//		System.out.println("email: " + Email + ", password: " + password);
		
		AdminDao adminDao = new AdminDao();
		Admin admin = adminDao.login(Email, password);
		
		if(admin != null) {
			//權限存session
			HttpSession session = request.getSession();
			session.setAttribute("adminId", admin.getAdminId());
			session.setAttribute("role", admin.getRole());
			//回傳前端
			Map<String, String> success = new HashMap<>();
			success.put("status", "success");
			success.put("role",admin.getRole());
			response.getWriter().write(new Gson().toJson(success));
		} else {
			Map<String, String> fail = new HashMap<>();
			fail.put("status", "fail");
			fail.put("message", "帳號或密碼錯誤");
			response.getWriter().write(new Gson().toJson(fail));
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
