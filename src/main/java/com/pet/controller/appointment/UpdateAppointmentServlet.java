package com.pet.controller.appointment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

import com.pet.dao.appointment.AppointmentDAO;
import com.pet.dao.appointment.PServiceDAO;
import com.pet.dao.member.MemberPetDao;
import com.pet.model.appoinment.AppointSuccessDTO;
import com.pet.model.appoinment.AppointmentBean;
import com.pet.model.appoinment.GetAllAppointmentDTO;
import com.pet.model.appoinment.MemberPetsBean;
import com.pet.model.appoinment.PetServiceBean;


@WebServlet("/UpdateAppointmentServlet")
public class UpdateAppointmentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 
   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	
		String appointmentIdStr = request.getParameter("appointmentId");
		String fuzzybyname = request.getParameter("fuzzybyname");
	    String searchById = request.getParameter("searchById");
		
		List<String> statusList = List.of("預約確認","進行中", "已完成", "已取消", "未到達");
	    List<String> payStatusList = List.of("待付款", "已付款", "已退款");
	    
	    AppointmentDAO appDao = new AppointmentDAO();	
		MemberPetDao memberPetdao = new MemberPetDao();
		PServiceDAO pserviceDAO = new PServiceDAO();
		
		GetAllAppointmentDTO dto = null;
		List<MemberPetsBean> petList = null;
		List<PetServiceBean> serviceList = null;
		
	
		try {
			Integer appointmentId = Integer.parseInt(appointmentIdStr);
			
			dto = appDao.SearchByAppointmentId(appointmentId);
			
			if (dto != null) {
	            int ownerMemberId = dto.getMemberId(); 
	            petList = memberPetdao.findByMemberId(ownerMemberId);	            	            
	            serviceList = pserviceDAO.SearchAll();
	        }
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			request.setAttribute("dto", dto);
			request.setAttribute("petList", petList);
			request.setAttribute("serviceList", serviceList);
			request.setAttribute("statusList", statusList);       
		    request.setAttribute("payStatusList", payStatusList);
			
		    request.setAttribute("fuzzybyname", fuzzybyname);
		    request.setAttribute("searchById", searchById);

			
			request.getRequestDispatcher("/admin/layout/UpdateAppointment.jsp").forward(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html;charset=UTF-8"); 
		
		String fuzzybyname = request.getParameter("fuzzybyname");
	    String searchById = request.getParameter("searchById");
		
		AppointmentDAO dao = new AppointmentDAO();
		
		try {
			
			String appointmentIdStr = request.getParameter("appointmentId");
			String status = request.getParameter("appointmentStatus");
			String petIdStr = request.getParameter("petId");
			String serviceIdStr = request.getParameter("serviceId"); 
			String employeeIdStr = request.getParameter("hiddenEmployeeId"); 
			String dateStr = request.getParameter("appointmentDate");  
			String slotIdStr = request.getParameter("hidden_slot_id");         
			String totalPriceStr = request.getParameter("totalPrice");
			String notes = request.getParameter("notes");
			String payStatus = request.getParameter("payStatus");
			String ratingStr = request.getParameter("rating");
			String comment = request.getParameter("comment");
			String reply = request.getParameter("reply");
			

			Integer appointmentId = Integer.parseInt(appointmentIdStr);
			Integer petId = Integer.parseInt(petIdStr);
			Integer serviceId = Integer.parseInt(serviceIdStr);
			Integer employeeId = Integer.parseInt(employeeIdStr);
			Integer slotId = Integer.parseInt(slotIdStr);
			Integer totalPrice = Integer.parseInt(totalPriceStr);
			Integer rating = Integer.parseInt(ratingStr);

			Date appointmentDate = Date.valueOf(dateStr);
	

			
			AppointmentBean app = new AppointmentBean();
			app.setAppointmentId(appointmentId);
			app.setPetId(petId);
			app.setServiceId(serviceId);
			app.setEmployeeId(employeeId);
			app.setSlotId(slotId);
			app.setAppointmentDate(appointmentDate);
			app.setNotes(notes);
			app.setAppointmentStatus(status);
			app.setRating(rating);
			app.setComment(comment);
			app.setReply(reply);
			app.setTotalPrice(totalPrice);
			app.setPayStatus(payStatus);

			dao.updateAppointment(app);
			
	
			String showMemberName = request.getParameter("memberName");
			String showPetName = request.getParameter("hiddenPetName");
			String showServiceName = request.getParameter("hiddenServiceName");
			String showDuration = request.getParameter("durationMinutes");
			String showEmpName = request.getParameter("employeeName");
			String showstartTime = request.getParameter("startTime"); 
			String showendTime = request.getParameter("endTime");
			
			
			AppointSuccessDTO successDto = new AppointSuccessDTO();
			successDto.setAppointmentId(appointmentId);
			successDto.setMemberName(showMemberName);
			successDto.setPetName(showPetName);
			successDto.setServiceName(showServiceName);
			successDto.setPrice(totalPriceStr);
			successDto.setNotes(notes);
			successDto.setEmpName(showEmpName);
			successDto.setAppointmentDateStr(dateStr);
			successDto.setAppointmentStatus(status);
			successDto.setPayStatus(payStatus); 
			successDto.setDurationMinutes(showDuration); 
			successDto.setAppointmentstartTime(showstartTime); 
			successDto.setAppointmentendTime(showendTime);
			successDto.setComment(comment); 
			successDto.setReply(reply); 
			successDto.setRating(ratingStr); 
			
			System.out.println("UpdateAppointmentServlet doPost傳出來的值:"+"\n"
					+ "appointmentId: " + appointmentId+"\n"
					+ "memberName: "+showMemberName+"\n"
					+ "petName: " +showPetName+"\n"
					+ "serviceName: "+showServiceName+"\n"
					+ "price: "+totalPriceStr +"\n"
					+ "notes: "+notes +"\n"
					+ "empName: "+showEmpName +"\n"
					+ "Appointment Date: "+dateStr +"\n"
					+ "Appointment Status: "+status +"\n"
					+ "slot_id: "+slotIdStr +"\n"
					+ "payStatus: "+payStatus +"\n"
					+ "Duration: " +showDuration +"\n"
					+ "startTime: "+showstartTime +"\n"
					+ "endTime: "+showstartTime +"\n"
					+ "comment: "+comment +"\n"
					+ "reply: "+ reply +"\n"
					+ "ratomg: "+ratingStr);	
			
			
			request.setAttribute("dto", successDto);
			request.setAttribute("fuzzybyname", fuzzybyname);
			request.setAttribute("searchById", searchById);
		
		
			request.getRequestDispatcher("/admin/layout/UpdateAppointmentSuccessful.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("修改失敗");
	}
	}
 }
