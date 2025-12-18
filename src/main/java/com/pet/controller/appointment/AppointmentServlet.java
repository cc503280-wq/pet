package com.pet.controller.appointment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.pet.dao.appointment.AppointmentDAO;
import com.pet.dao.appointment.PetServiceDAO;
import com.pet.dao.member.MemberDao;
import com.pet.dao.member.MemberPetDao;
import com.pet.model.appointment.AppointSuccessDTO;
import com.pet.model.appointment.Appointment;
import com.pet.model.appointment.GetAllAppointmentDTO;
import com.pet.utils.HibernateUtil;




@WebServlet("/AppointmentServlet.do")
public class AppointmentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processAction(request,response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processAction(request,response);
	}


	private void processAction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 1. 取得目前的 Session (由 Filter 已經開啟並管理 Transaction)
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.getCurrentSession();

        String action = request.getParameter("action");
        if (action == null) action = "list"; 

        // 2. 實例化 DAO (傳入 Session 以便共用同一個連線)
        AppointmentDAO appDao = new AppointmentDAO(session);

        try {
            switch (action) {
                case "list":
                    listAppointments(request, response, appDao);
                    break;
                case "delete":
                    deleteAppointment(request, response, appDao);
                    break;
//                case "toInsert":
//                    showInsertForm(request, response, session);
//                    break;
//                case "insert":
//                    insertAppointment(request, response, appDao);
//                    break;
//                case "toUpdate":
//                    showUpdateForm(request, response, appDao, session);
//                    break;
//                case "update":
//                    updateAppointment(request, response, appDao);
//                    break;
                default:
                    listAppointments(request, response, appDao);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e); 
        }
    }
	
	// ============================================================
    // 1. 列表查詢 (List)
    // ============================================================
    private void listAppointments(HttpServletRequest request, HttpServletResponse response, AppointmentDAO appDao) throws ServletException, IOException {
        String searchById = request.getParameter("searchById");
        String fuzzybyname = request.getParameter("fuzzybyname"); 

        List<GetAllAppointmentDTO> apps;

        if (searchById != null && !searchById.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(searchById.trim());
                apps = new java.util.ArrayList<>();
                GetAllAppointmentDTO dto = appDao.searchByAppointmentId(id);
                if (dto != null) apps.add(dto);
            } catch (NumberFormatException e) {
                apps = appDao.findAllAppointmentDTOs();
            }
        }else if (fuzzybyname != null && !fuzzybyname.trim().isEmpty()) {
            
             apps = appDao.getFuzzySearchByName(fuzzybyname.trim());   
        }else {
            apps = appDao.findAllAppointmentDTOs();
        }
        
        request.setAttribute("searchById", searchById);
        request.setAttribute("fuzzybyname", fuzzybyname);

        request.setAttribute("apps", apps);
        request.getRequestDispatcher("/admin/layout/GetAllAppointment.jsp").forward(request, response);
    }

    // ============================================================
    // 刪除 (Delete)
    // ============================================================
    private void deleteAppointment(HttpServletRequest request, HttpServletResponse response, AppointmentDAO appDao) throws IOException {
        String appointmentIdStr = request.getParameter("appointmentId");
        String actionType = request.getParameter("type");
        
        
        if (appointmentIdStr != null && !appointmentIdStr.isEmpty()) {
            try {
                int appointmentId = Integer.parseInt(appointmentIdStr);
                
                if ("soft".equals(actionType)) {
                    // 軟刪除
                    appDao.SoftDelByAppointmentId(appointmentId);
                } else {
                    // 硬刪除
                    appDao.delByAppointmentId(appointmentId); 
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        StringBuilder redirectURL = new StringBuilder("AppointmentServlet.do?action=list");
        String fuzzyByName = request.getParameter("fuzzybyname");
        String searchById = request.getParameter("searchById");

        if (fuzzyByName != null && !fuzzyByName.trim().isEmpty()) {
            redirectURL.append("&fuzzybyname=").append(URLEncoder.encode(fuzzyByName, StandardCharsets.UTF_8));
        }
        if (searchById != null && !searchById.trim().isEmpty()) {
            redirectURL.append("&searchById=").append(searchById);
        }

        response.sendRedirect(redirectURL.toString());
    }

   

}
