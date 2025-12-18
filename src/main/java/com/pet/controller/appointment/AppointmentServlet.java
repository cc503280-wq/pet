package controller;

import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.GetAllAppointmentDTO;
import java.io.IOException;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import DAO.AppointmentDAO1;
import Util.HibernateUtil;


@WebServlet("/AppointmentServlet.do")
public class AppointmentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processAction(request,response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processAction(request,response);
	}


	private void processAction(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.getCurrentSession();
		
		String searchById = request.getParameter("searchById");
	    String fuzzybyname = request.getParameter("fuzzybyname");
	    
	    AppointmentDAO1 appDao = new AppointmentDAO1(session); 
	    List<GetAllAppointmentDTO> apps;

	    if (searchById != null && !searchById.trim().isEmpty()) {
	        int id = Integer.parseInt(searchById.trim());
	        apps = new java.util.ArrayList<>();
	        GetAllAppointmentDTO dto = appDao.searchByAppointmentId(id);
	        if(dto != null) apps.add(dto);
	    } else {
	        apps = appDao.findAllAppointmentDTOs();
	    }
	    
	    request.setAttribute("apps", apps);
	    request.getRequestDispatcher("/admin/layout/GetAllAppointment.jsp").forward(request, response);

	}

}
