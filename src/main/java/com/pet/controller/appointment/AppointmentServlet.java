package com.pet.controller.appointment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.pet.dao.appointment.AppointmentDAO;
import com.pet.dao.appointment.PetServiceDAO;
import com.pet.dao.member.MemberDao;
import com.pet.dao.member.MemberPetDao;
import com.pet.model.appointment.AppointSuccessDTO;
import com.pet.model.appointment.Appointment;
import com.pet.model.appointment.Employee;
import com.pet.model.appointment.GetAllAppointmentDTO;
import com.pet.model.appointment.PetService;
import com.pet.model.appointment.WorkSlot;
import com.pet.model.member.Member;
import com.pet.model.member.MemberPet;
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

        
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.getCurrentSession();

        String action = request.getParameter("action");
        if (action == null) action = "list"; 

       
        AppointmentDAO appDao = new AppointmentDAO(session);

        try {
            switch (action) {
                case "list":
                    listAppointments(request, response, appDao);
                    break;
                case "delete":
                    deleteAppointment(request, response, appDao);
                    break;
                case "toInsert":
                    showInsertForm(request, response, session);
                    break;
                    
                case "toUpdate":
                	showUpdateForm(request, response, appDao, session);
                	break;              	
              case "insert":
                  	insertAppointment(request, response, appDao,session);
                  	break;
                
              case "update":
                  	updateAppointment(request, response, appDao,session);
                  	break;
                default:
                    listAppointments(request, response, appDao);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e); 
        }
    }
	
	// ============================================================
    // 列表查詢 (List)
    // ============================================================
    private void listAppointments(HttpServletRequest request, HttpServletResponse response, AppointmentDAO appDao) throws ServletException, IOException {
        String searchById = request.getParameter("searchById");
        String fuzzybyname = request.getParameter("fuzzybyname"); 

        List<GetAllAppointmentDTO> apps;

        if (searchById != null && !searchById.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(searchById.trim());
                apps = new ArrayList<>();
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
    
    // ============================================================
    // 新增 (showInsertForm)
    // ============================================================
    
    private void showInsertForm(HttpServletRequest request, HttpServletResponse response, Session session) throws IOException, ServletException {
    	
    	String selectedMemberIdStr = request.getParameter("selectedMemberId");
    	
    	MemberPetDao memberPetDao = new MemberPetDao(session);
    	MemberDao memberDao = new MemberDao(session);
    	PetServiceDAO pserviceDAO = new PetServiceDAO(session);
    	
    	List<Member> memberList = null;
    	List<PetService> serviceList = null;
    	List<MemberPet> petList = null;
    	
		
		try {
			memberList = memberDao.queryAllMembers();
			serviceList = pserviceDAO.getAllService();
			
			if (selectedMemberIdStr != null && !selectedMemberIdStr.isEmpty()) {
                int selectedMemberId = Integer.parseInt(selectedMemberIdStr);

                Member currentMember = memberPetDao.getMemberWithPet(selectedMemberId);
 
                request.setAttribute("currentMemberId", selectedMemberId);
                request.setAttribute("memberName", currentMember.getName());
                
                petList = currentMember.getPets();           
            }
	
		} catch (Exception e) {

			e.printStackTrace();
		} 
		
		request.setAttribute("memberList", memberList);
		request.setAttribute("petList", petList);
		request.setAttribute("serviceList", serviceList);
		request.getRequestDispatcher("/admin/layout/InsertAppointment.jsp").forward(request, response);
 
    }
    
  // ============================================================
  // 更新 (showUpdateForm)
  // ============================================================
  
  protected void showUpdateForm(HttpServletRequest request, HttpServletResponse response, AppointmentDAO appDao, Session session) throws ServletException, IOException {
		
  	
		String appointmentIdStr = request.getParameter("appointmentId");
		String fuzzybyname = request.getParameter("fuzzybyname");
	    String searchById = request.getParameter("searchById");
		
		List<String> statusList = List.of("預約確認","進行中", "已完成", "已取消", "未到達");
	    List<String> payStatusList = List.of("待付款", "已付款", "已退款");
	    
	    AppointmentDAO app = new AppointmentDAO(session);	
	    MemberPetDao memberPetDao = new MemberPetDao(session);
		PetServiceDAO pserviceDAO = new PetServiceDAO(session);
		
		GetAllAppointmentDTO dto = null;
		List<MemberPet> petList = null; 
		List<PetService> serviceList = null;
	
		try {
			Integer appointmentId = Integer.parseInt(appointmentIdStr);
			
			dto = app.searchByAppointmentId(appointmentId);
			
			if (dto != null) {
				serviceList = pserviceDAO.getAllService();
	            petList = memberPetDao.getMemberWithPet(dto.getMemberId()).getPets();
	        }
		
		} catch (Exception e) {
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
  
  
    
 // ============================================================
 // 新增 (insertTo)
 // ============================================================
    
    protected void insertAppointment(HttpServletRequest request, HttpServletResponse response, AppointmentDAO appDao,Session session) throws ServletException, IOException {
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
        Integer slotId = Integer.parseInt(slot_idStr);
        Date appointmentDate = null;
        if (appointmentDateStr != null && !appointmentDateStr.trim().isEmpty()) {
             try {
              Date utilDate = formatter.parse(appointmentDateStr);
              appointmentDate = new java.sql.Date(utilDate.getTime());
                	
        } catch (DateTimeParseException e) {
               e.printStackTrace();
        } catch (ParseException e) {
			   e.printStackTrace();
		}
       }

            Appointment app = new Appointment();
            MemberPet pet = session.find(MemberPet.class, petId);
            app.setMemberPets(pet);
            PetService service = session.find(PetService.class, serviceId);
            app.setPetservice(service);
            Employee emp = session.find(Employee.class, employeeId);
            app.setEmployee(emp);
            WorkSlot ws =session.find(WorkSlot.class, slotId);
            app.setWorkSlot(ws);
            
            app.setAppointmentDate(appointmentDate);
            app.setNotes(notes);
            app.setAppointmentStatus(appointmentStatus);
            app.setTotalPrice(price);
            
            appDao.insertAppointment(app);
            
            String showMemberName = request.getParameter("memberName");
            System.out.println("showMemberName:"+showMemberName);
            String showPetName = request.getParameter("hiddenPetName");
            String showServiceName = request.getParameter("hiddenServiceName");
            String showDuration = request.getParameter("durationMinutes");
            String showEmpName = request.getParameter("employeeName");  
            String showAppointmentstartTime = request.getParameter("appointmentstartTime");
            String showAppointmentendTime = request.getParameter("appointmentendTime");        
            
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
    

 // ============================================================
 // 更新 (updateAppointment)
 // ============================================================

	
	protected void updateAppointment(HttpServletRequest request, HttpServletResponse response, AppointmentDAO appDao, Session session) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html;charset=UTF-8");
		
		String fuzzybyname = request.getParameter("fuzzybyname");
	    String searchById = request.getParameter("searchById");
		
		AppointmentDAO dao = new AppointmentDAO(session);
		
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
			Integer rating = (ratingStr != null && !ratingStr.trim().isEmpty()) ? Integer.parseInt(ratingStr) : 0;
			
			java.sql.Date appointmentDate = null;
			if (dateStr != null && !dateStr.trim().isEmpty()) {
			    try {
			        appointmentDate = java.sql.Date.valueOf(dateStr);
			    } catch (IllegalArgumentException e) {
			        System.out.println("日期格式錯誤: " + dateStr);
			    }
			}
			
	

			
			Appointment app = new Appointment();
			app.setAppointmentId(appointmentId);
			MemberPet pet = session.find(MemberPet.class, petId);
            app.setMemberPets(pet);
            PetService service = session.find(PetService.class, serviceId);
            app.setPetservice(service);
            Employee emp = session.find(Employee.class, employeeId);
            app.setEmployee(emp);
            WorkSlot ws =session.find(WorkSlot.class, slotId);
            app.setWorkSlot(ws);
            
			
			
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
