package com.pet.controller.appointment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import com.google.gson.Gson;
import com.pet.dao.appointment.EmpDAO;
import com.pet.dao.appointment.ScheduleQueryDAO;
import com.pet.model.appoinment.EmpBean;

@WebServlet("/GetScheduleServlet")
public class GetScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private EmpDAO empDao = new EmpDAO();
	private final Gson gson = new Gson();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// 取得所有在職的美容師列表
			List<EmpBean> emps = empDao.getAllActiveEmp(); 
			
		    String dateParam = request.getParameter("AppointDate");
		    LocalDate baseDate;

		    
		    if (dateParam != null && !dateParam.isEmpty()) {
		        try {
		            
		            baseDate = LocalDate.parse(dateParam);
		        } catch (DateTimeParseException e) {
		           
		            baseDate = LocalDate.now();
		        }
		    } else {
		        
		        baseDate = LocalDate.now();
		    }
			
		
			request.setAttribute("emps", emps);
			request.setAttribute("AppointDate", baseDate);
			
			
			request.getRequestDispatcher("/admin/layout/selectempschedule.jsp").forward(request, response);
			
		} catch (SQLException | NamingException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "無法載入美容師資料");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String employeeIdStr = request.getParameter("employeeId");
		String startDateStr = request.getParameter("startDate");
		
		Integer employeeIdFilter = null;
		
		if (employeeIdStr != null && !employeeIdStr.isEmpty()) {
			try {
				employeeIdFilter = Integer.parseInt(employeeIdStr);
			} catch (NumberFormatException e) {
				System.out.println("ID 格式錯誤: " + employeeIdStr);
			}
		}
		
		
		if (startDateStr == null || startDateStr.isEmpty() || !startDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
	        System.out.println("⚠️ 收到無效日期格式: [" + startDateStr + "]，系統自動改用今天。");
	        startDateStr = java.time.LocalDate.now().toString(); 
	    }
	    
		
		System.out.println("收到查詢請求 - ID: " + employeeIdFilter + ", 日期: " + startDateStr);

		
		ScheduleQueryDAO dao = new ScheduleQueryDAO();
		List<Map<String, Object>> tabulatorData = null;
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		try {
			tabulatorData = dao.generateTabulatorSchedule(employeeIdFilter, startDateStr);
			
			
			String jsonOutput = gson.toJson(tabulatorData);
			System.out.println("後端回傳 JSON: " + jsonOutput); 
			
			response.getWriter().write(jsonOutput);
			
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("{\"error\": \"資料庫查詢失敗: " + e.getMessage() + "\"}");
		}
	}
}