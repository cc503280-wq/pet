package com.pet.controller.appointment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.sql.Date;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.naming.NamingException;

import com.pet.dao.appointment.AppointmentDAO;
import com.pet.dao.appointment.PServiceDAO;
import com.pet.dao.member.MemberDao;
import com.pet.dao.member.MemberPetDao;
import com.pet.model.appoinment.AppointSuccessDTO;
import com.pet.model.appoinment.AppointmentBean;
import com.pet.model.appoinment.MemberBean;
import com.pet.model.appoinment.MemberPetsBean;
import com.pet.model.appoinment.PetServiceBean;

@WebServlet("/InsertAppointmentServlet")
public class InsertAppointmentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String selectedMemberIdStr = request.getParameter("selectedMemberId");
	

		MemberDao memberdao = new MemberDao();
		List<MemberBean> memberList = null;
		
		MemberPetDao memberPetdao = new MemberPetDao();
		List<MemberPetsBean> petList = null;
		
		PServiceDAO pserviceDAO = new PServiceDAO();
		List<PetServiceBean> serviceList = null;
		
		
		
		try {
			memberList = memberdao.SearchAll();
			serviceList = pserviceDAO.SearchAll();
			
			if (selectedMemberIdStr != null && !selectedMemberIdStr.isEmpty()) {
                int selectedMemberId = Integer.parseInt(selectedMemberIdStr);

                petList = memberPetdao.findByMemberId(selectedMemberId);
 
                request.setAttribute("currentMemberId", selectedMemberId);
                
                String memberName = memberdao.getMemberNameById(selectedMemberId);
                request.setAttribute("memberName", memberName);
            }
			
			
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("memberList", memberList);
		request.setAttribute("petList", petList);
		request.setAttribute("serviceList", serviceList);
		request.getRequestDispatcher("/admin/layout/InsertAppointment.jsp").forward(request, response);
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        
        String petIdStr = request.getParameter("petId");
        String serviceIdStr = request.getParameter("serviceId");
        String employeeIdStr = request.getParameter("hiddenEmployeeId");
        String appointmentDateStr = request.getParameter("appointmentDate");
        String slot_idStr = request.getParameter("hidden_slot_id");
        String notes = request.getParameter("notes");
        String PriceStr = request.getParameter("totalPrice");
        String appointmentStatus = "預約確認";
        
       
        
        Integer petId = Integer.parseInt(petIdStr);
        Integer serviceId = Integer.parseInt(serviceIdStr);
        Integer employeeId = Integer.parseInt(employeeIdStr);
        Integer price = Integer.parseInt(PriceStr);
        Integer slot_id = Integer.parseInt(slot_idStr);
        Date appointmentDate = null;
        if (appointmentDateStr != null && !appointmentDateStr.trim().isEmpty()) {
             try {
              java.util.Date utilDate = formatter.parse(appointmentDateStr);
              appointmentDate = new java.sql.Date(utilDate.getTime());
                	
        } catch (DateTimeParseException e) {
               e.printStackTrace();
        } catch (ParseException e) {
			   e.printStackTrace();
		}
       }

            AppointmentBean app = new AppointmentBean();
            app.setPetId(petId);
            app.setServiceId(serviceId);
            app.setEmployeeId(employeeId);
            app.setSlotId(slot_id);
            app.setAppointmentDate(appointmentDate);
            app.setNotes(notes);
            app.setAppointmentStatus(appointmentStatus);
            app.setTotalPrice(price);
            
            System.out.println(app);
             
            AppointmentDAO dao = new AppointmentDAO(); 
            try {
				dao.insertAppointment(app);
			} catch (SQLException | NamingException e) {
				e.printStackTrace();
			}
            
            String memberIdStr = request.getParameter("memberId");
            MemberDao memberdao = new MemberDao();
            Integer memberId = Integer.parseInt(memberIdStr);
            String showMemberName="";
			try {
				showMemberName = memberdao.getMemberNameById(memberId);
			} catch (SQLException | NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
           
            String showPetName = request.getParameter("hiddenPetName");
            String showServiceName = request.getParameter("hiddenServiceName");
            String showDuration = request.getParameter("durationMinutes");
            String showEmpName = request.getParameter("employeeName");  
            String showAppointmentstartTime = request.getParameter("appointmentstartTime");
            String showAppointmentendTime = request.getParameter("appointmentendTime");
            
            System.out.println("showMemberName: "+showMemberName);
            System.out.println("showPetName: "+showPetName);
            System.out.println("showServiceName: "+showServiceName);    
            System.out.println("showNotes: "+notes);
            System.out.println("showEmpName: "+showEmpName);
            System.out.println("showAppointmentstartTime: "+showAppointmentstartTime);
            System.out.println("showAppointmentendTime: "+showAppointmentendTime);
            
            
            
            
            AppointSuccessDTO dto = new AppointSuccessDTO();
            
            dto.setMemberName(showMemberName);
            dto.setPetName(showPetName);
            dto.setServiceName(showServiceName);
            dto.setPrice(PriceStr);
            dto.setDuration(showDuration);
            dto.setNotes(notes);
            dto.setEmpName(showEmpName);
            dto.setAppointmentDateStr(appointmentDateStr);
            dto.setAppointmentstartTime(showAppointmentstartTime);
            dto.setAppointmentendTime(showAppointmentendTime);
     
            
            request.setAttribute("dto",dto);
            request.getRequestDispatcher("/admin/layout/InsertAppointmentSuccessful.jsp").forward(request, response);

           

	}


}
