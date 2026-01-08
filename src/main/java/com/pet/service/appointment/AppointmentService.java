package com.pet.service.appointment;

import com.cloudinary.Cloudinary;

import com.mysql.cj.log.Log;
import com.pet.dao.appointment.*;
import com.pet.dao.member.MemberPetRepository;
import com.pet.dao.member.MemberRepository;
import com.pet.model.appointment.Appointment;
import com.pet.model.appointment.AppointmentDetailList;
import com.pet.model.appointment.AppointmentDetails;
import com.pet.model.appointment.AppointmentList;
import com.pet.model.appointment.AppointmentRequest;
import com.pet.model.appointment.DailySchedule;
import com.pet.model.appointment.ServiceItem;
import com.pet.model.member.Member;
import com.pet.model.member.MemberPet;
import com.pet.util.TimeSlotUtils;

import lombok.extern.slf4j.Slf4j;

import java.lang.System.Logger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class AppointmentService {
	@Autowired
    private DailyScheduleRepository dailyScheduleRepository;

    private final Cloudinary cloudinary;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private AppointmentListRepository appointmentListRepository;

	@Autowired
	private ServiceItemRepository serviceItemRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberPetRepository memberPetRepository;
	
	@Autowired
	private AppointmentDetailListRepository appointmentDetailListRepository;

    AppointmentService(Cloudinary cloudinary, DailyScheduleRepository dailyScheduleRepository) {
        this.cloudinary = cloudinary;
        this.dailyScheduleRepository = dailyScheduleRepository;
    }

	public List<AppointmentList> getAllAppointments() {
		return appointmentListRepository.findAll();
	}
	
	public List<Member> findAllMembers() {
		return memberRepository.findAll();
	}
	
	public List<MemberPet> getPetsByMemberId(Integer memberId) {
		return memberPetRepository.findByMemberMemberId(memberId);
	}

	public List<ServiceItem> findServicesByPetTypeAndPetSize(String petType, String petSize) {
		List<String> sizeCriteria = Arrays.asList(petSize, "不分體型");
		return serviceItemRepository.findByTargetPetTypeAndTargetPetSizeInAndIsActiveTrue(petType,sizeCriteria);
	}
	
	//=======取消訂單
	@Transactional
	public Appointment CancelAppointment(Integer id) {

		Appointment existing = appointmentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的預約"));
		String cancelStatus = "已取消";
		existing.setAppointmentStatus(cancelStatus);
		 Appointment saveResult = appointmentRepository.save(existing);
		 
		 if(saveResult!=null) {
		 log.info("預約單號："+existing.getAppointmentId()+"取消預約");
		 Integer groomerId = existing.getGroomerId();
		 LocalDate cancelDate = existing.getAppointmentDate();
		 LocalTime cancelStartTime = existing.getStartTime();
		 LocalTime cancelEndTime = existing.getEndTime();
		 
		 long longDuration = Duration.between(cancelStartTime, cancelEndTime).toMinutes();
		 int targetDuration = (int)longDuration;
		 
		 Optional<DailySchedule> scheudle = dailyScheduleRepository.findByGroomerIdAndWorkDate(groomerId,cancelDate);

		 if(scheudle.isPresent()) {
			DailySchedule targetscheudle = scheudle.get();
			log.info("已取出GroomerId:"+groomerId+"timeSlot");
			
			String targetTimeSlot  = targetscheudle.getTimeSlots();
			
			 String unLockResult = TimeSlotUtils.unLockSlots(targetTimeSlot, TimeSlotUtils.timeToStartIndex(cancelStartTime), targetDuration);
		 
			 targetscheudle.setTimeSlots(unLockResult);
			 
			 DailySchedule result = dailyScheduleRepository.save(targetscheudle);
			 
			 if(result!=null) {
				 log.info("已將美容師:"+groomerId+
						  "工作日:"+cancelDate+
						  "時段"+cancelStartTime+"~"+cancelEndTime+"解鎖");	
			 }else{
				 log.error("鎖定失敗，請檢查");
			 }
		 }else {
			 log.error("預約單號："+existing.getAppointmentId()+"取消預約失敗，請檢查");
		 }
		
		 
		 }
		
		return saveResult; 
	}

	
	
	
	
	//新增預約訂單
	@Transactional
	public void saveAppointment(AppointmentRequest request) {
		
		Appointment appointment = new Appointment();
		
		String lockStartTime = request.getStartTime();		
		String lockedTimeSlots ="";
		
		appointment.setPetId(request.getPetId());
		appointment.setGroomerId(request.getGroomerId());
		appointment.setAppointmentDate(request.getAppointmentDate());
		appointment.setStartTime(LocalTime.parse(request.getStartTime()));
		appointment.setEndTime(LocalTime.parse(request.getEndTime()));
		appointment.setFinalPrice(request.getTotalPrice());
		appointment.setNotes(request.getNotes());
		appointment.setAppointmentStatus("預約確認");
		
		
		Integer totalDuration = 0;
		
		List<Integer> serviceIds = request.getServiceIds();
		if (serviceIds != null && !serviceIds.isEmpty()) {
			List<ServiceItem> services = serviceItemRepository.findAllById(serviceIds);

			for (ServiceItem svc : services) {
				AppointmentDetails detail = new AppointmentDetails();

			
				detail.setServiceId(svc.getServiceId());
				detail.setPrice(svc.getPrice());
				detail.setDurationMinutes(svc.getDurationMinutes());				
				detail.setAppointment(appointment); 
				appointment.getAppointmentDetails().add(detail);
				totalDuration = totalDuration + svc.getDurationMinutes();
				
			}
		}
		 Appointment result= appointmentRepository.save(appointment);
		 
		 log.info("預約單號:"+appointment.getAppointmentId()+"預約時間:"+"時長:"+totalDuration);
		 
		 if(result!= null&& result.getAppointmentId()!=null) {
			
			 
			 Optional<DailySchedule> scheudle = dailyScheduleRepository.findByGroomerIdAndWorkDate(appointment.getGroomerId(),appointment.getAppointmentDate());
			
			 if(scheudle.isPresent()) {
				 DailySchedule targetscheudle= scheudle.get();
				 
				String targetTimeSlot  = targetscheudle.getTimeSlots();
				 
				  lockedTimeSlots = TimeSlotUtils.lockSlots(targetTimeSlot, TimeSlotUtils.timeToStartIndex(lockStartTime), totalDuration );
			 
			 				 
				 targetscheudle.setTimeSlots(lockedTimeSlots);
				 DailySchedule lockResult = dailyScheduleRepository.save(targetscheudle);
				 log.info("預約與明細已一併儲存，單號："+appointment.getAppointmentId()+"新增成功");
				 if(lockResult!=null) {
				 log.info("已將美容師:"+appointment.getGroomerId()+
						  "工作日:"+appointment.getAppointmentDate()+
						  "時段"+appointment.getStartTime()+"~"+appointment.getEndTime()+"鎖定");	 
			 }else {
				 log.error("timeSlots鎖定失敗，請檢查");
				}			 
			 }
		 }else {
			 log.error("單號："+appointment.getAppointmentId()+"新增失敗");
		 }

		
	}
	
	//====Appointment 查詢類====
	public List<AppointmentList> searchAppointments(String memberPhone, String status, String startDate,
			String endDate) {
		return appointmentListRepository.complexSearch(memberPhone, status, startDate, endDate);
	}
	
	public List<AppointmentDetailList> getAllDetails(){
		return  appointmentDetailListRepository.findAll();
	}
	
	public List<AppointmentDetailList> getDetailsByAppointmentId(Integer appointmentId) {
        return appointmentDetailListRepository.findByAppointmentId(appointmentId);
    }

}