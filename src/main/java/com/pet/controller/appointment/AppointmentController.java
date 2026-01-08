package com.pet.controller.appointment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.pet.model.appointment.AppointmentDetailList;
import com.pet.model.appointment.AppointmentList;
import com.pet.model.appointment.AppointmentRequest;
import com.pet.model.appointment.ServiceItem;
import com.pet.model.member.Member;
import com.pet.model.member.MemberPet;
import com.pet.service.appointment.AppointmentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/appointments") 
public class AppointmentController {

	@Autowired
	private AppointmentService appointmentService;
	

	@GetMapping
	public ResponseEntity<List<AppointmentList>> getAllAppointments() {
		List<AppointmentList> list = appointmentService.getAllAppointments();
		return ResponseEntity.ok(list);
	}
	
	@GetMapping(path = "/search")
	public ResponseEntity<List<AppointmentList>> searchAppointments(
	        @RequestParam(required = false) String memberPhone,
	        @RequestParam(required = false) String status,
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate) {
	    
	    List<AppointmentList> list = appointmentService.searchAppointments(memberPhone, status, startDate, endDate);
	    return ResponseEntity.ok(list);
	}

	
	@PatchMapping(path = "/cancel/{appointmentId}")
	@ResponseBody
	public ResponseEntity<String> CancelAppointment(@PathVariable Integer appointmentId) {
		log.info("API: 修改 ID {} 的美容師資料", appointmentId);
		appointmentService.CancelAppointment(appointmentId);
		return ResponseEntity.ok("Cancelled Successfully");
	}

	
	@GetMapping("/insert")
	public String showAddPage(Model model) {
		List<Member> members = appointmentService.findAllMembers();
		model.addAttribute("memberList", members);
		return "admin/InsertAppointment";
	}
	
	@PostMapping("/insertInto")
	@ResponseBody
	public ResponseEntity<?> insertAppointment(@RequestBody AppointmentRequest request) {
	    try {
	        appointmentService.saveAppointment(request);
	        return ResponseEntity.ok().body("預約建立成功");
	    } catch (Exception e) {
	        e.printStackTrace();	
	        return ResponseEntity.badRequest().body("預約失敗：" + e.getMessage());
	    }  
	}

	@GetMapping("/{id}/pets")
	@ResponseBody
	public List<MemberPet> getMemberPets(@PathVariable Integer id) {
		return appointmentService.getPetsByMemberId(id);
	}

	@GetMapping("/services")
	@ResponseBody 
	public List<ServiceItem> getServices(@RequestParam String petType,@RequestParam String petSize) {
		return appointmentService.findServicesByPetTypeAndPetSize(petType,petSize);
	}
	
	//=======Appointment_details======
	
	@GetMapping("/details")
	@ResponseBody
	public List<AppointmentDetailList> getAllDetails(){
		return appointmentService.getAllDetails();
	}
	
	@GetMapping("/details/{id}")
    @ResponseBody
    public ResponseEntity<List<AppointmentDetailList>> getDetailsByAppointmentId(@PathVariable Integer id) {
        List<AppointmentDetailList> details = appointmentService.getDetailsByAppointmentId(id);
        return ResponseEntity.ok(details);
    }
	
	

}