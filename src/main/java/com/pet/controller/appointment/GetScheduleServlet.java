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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.google.gson.Gson;
import com.pet.dao.appointment.EmpDAO;
import com.pet.dao.appointment.ScheduleQueryDAO;
import com.pet.model.appointment.Employee;
import com.pet.utils.HibernateUtil;

@WebServlet("/GetScheduleServlet.do")
public class GetScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final Gson gson = new Gson();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processAction(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processAction(request, response);
	}

	private void processAction(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");

		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.getCurrentSession();

		String action = request.getParameter("action");
		if (action == null) {
			action = "view";
		}

		ScheduleQueryDAO scheduleDao = new ScheduleQueryDAO(session);

		try {
			switch (action) {
			case "data":
				getScheduleData(request, response, scheduleDao);
				break;

			case "view":
			default:
				showSchedulePage(request, response, session);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if ("data".equals(action)) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.setContentType("application/json;charset=UTF-8");
				response.getWriter().write("{\"error\": \"系統發生錯誤: " + e.getMessage() + "\"}");
			} else {
				throw new ServletException(e);

			}
		}
	}

	// ============================================================
	// 顯示排班頁面 
	// ============================================================
	private void showSchedulePage(HttpServletRequest request, HttpServletResponse response, Session session)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");

		try {
			
			EmpDAO empDao = new EmpDAO(session);
            List<Employee> emps = empDao.getAllActiveEmp();
            request.setAttribute("emps", emps);

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

			
			request.setAttribute("AppointDate", baseDate);

			request.getRequestDispatcher("/admin/layout/selectempschedule.jsp").forward(request,
					response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "無法載入美容師資料");
		}

	}

	// ============================================================
	// 取得排班資料 JSON 
	// ============================================================
	private void getScheduleData(HttpServletRequest request, HttpServletResponse response, ScheduleQueryDAO dao)
			throws IOException {

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

		try {
			List<Map<String, Object>> tabulatorData = dao.generateTabulatorSchedule(employeeIdFilter, startDateStr);

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