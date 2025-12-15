package service;

import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import javax.naming.NamingException;
import java.net.URLEncoder;
import dao.AppointmentDAO;




@WebServlet("/DeleteAppointmentServlet")
public class DeleteAppointmentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private AppointmentDAO dao = new AppointmentDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String appointmentIdStr = request.getParameter("appointmentId");
		String searchById = request.getParameter("searchById");
	    String fuzzyByName = request.getParameter("fuzzybyname");
	    String actionType = request.getParameter("type");
	    		
		if (appointmentIdStr != null) {
            try {
                int appointmentId = Integer.parseInt(appointmentIdStr);
                
               
                if ("soft".equals(actionType)) {
                    
                    dao.SoftDelByAppointmentId(appointmentId);
                } else {
                   
                    dao.DelByAppointmentId(appointmentId);
                }
                
            } catch (SQLException | NamingException | NumberFormatException e) {
                e.printStackTrace();
            }
        }

        
		
		StringBuilder redirectURL = new StringBuilder("GetAllAppointmentsServlet");
		boolean isFirstParam = true; 

		
		if (fuzzyByName != null && !fuzzyByName.trim().isEmpty()) {
		    String encodedName = URLEncoder.encode(fuzzyByName, "UTF-8");
		    
		  
		    redirectURL.append(isFirstParam ? "?" : "&");
		    redirectURL.append("fuzzybyname=").append(encodedName);
		    
		    isFirstParam = false;
		}

		
		if (searchById != null && !searchById.trim().isEmpty()) {
		    
		   
		    redirectURL.append(isFirstParam ? "?" : "&");
		    redirectURL.append("searchById=").append(searchById);
		    
		    isFirstParam = false;
		}

		
		response.sendRedirect(redirectURL.toString());
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}


