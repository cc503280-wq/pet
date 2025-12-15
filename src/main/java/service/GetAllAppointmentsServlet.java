package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import dao.AppointmentDAO;
import bean.GetAllAppointmentDTO;


@WebServlet("/GetAllAppointmentsServlet")
public class GetAllAppointmentsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	        String searchById = request.getParameter("searchById");
	        String fuzzybyname = request.getParameter("fuzzybyname");
	        
	        List<GetAllAppointmentDTO> apps = new ArrayList<>();
	        AppointmentDAO appDao = new AppointmentDAO();
	        
	        try {
	        	
	            if (searchById != null && !searchById.trim().isEmpty()) {
	                try {
	                	
	                    int id = Integer.parseInt(searchById.trim());
	                    
	                    apps.add(appDao.SearchByAppointmentId(id));
	                    
	                } catch (NumberFormatException e) {
	                    System.out.println("ID 格式錯誤");
	                }
	            
	            } else if (fuzzybyname != null && !fuzzybyname.trim().isEmpty()) {
	                apps = appDao.getFuzzySearchByName(fuzzybyname.trim());
	            } else {
	                apps = appDao.getAllAppointment();
	            }
	            
	           
	            request.setAttribute("apps", apps);
	            request.setAttribute("fuzzybyname", fuzzybyname);
	            request.setAttribute("searchById", searchById);

	            
	            request.getRequestDispatcher("/admin/layout/GetAllAppointment.jsp").forward(request, response);
	            
	        } catch (SQLException | NamingException e) {
	            e.printStackTrace();
	        }
	    }

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
