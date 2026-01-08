package com.pet.service.appointment;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.Cloudinary;
import com.pet.dao.appointment.AppointmentRepository;
import com.pet.dao.appointment.DailyScheduleRepository;
import com.pet.dao.appointment.GroomerRepository;
import com.pet.dao.appointment.LeaveRecordGroomerViewRepository;
import com.pet.dao.appointment.LeaveRecordRepository;
import com.pet.model.appointment.Groomer;
import com.pet.model.appointment.LeaveRecord;
import com.pet.model.appointment.LeaveRecoredGroomerView;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroomerService {
	@Autowired
	private GroomerRepository groomerRepository;

	@Autowired
	private DailyScheduleRepository dailyScheduleRepository;

	@Autowired
	private LeaveRecordRepository leaveRecordRepository;

	@Autowired
	private LeaveRecordGroomerViewRepository leaveRecordGroomerViewRepository;
	@Autowired
	private Cloudinary cloudinary;
	
	@Autowired
	private AppointmentRepository appointmentRepository;


	
	public List<Groomer> getAllGroomer() {
		return groomerRepository.findAll();
	}
	
	public Groomer getGroomerById(Integer id) {
        return groomerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的美容師"));
    }


	@Transactional
    public Groomer saveGroomerInfo(Groomer groomer, MultipartFile file) throws IOException {
        log.info("開始新增美容師: {}", groomer.getGroomerName());

        Optional<Groomer> existPhone = groomerRepository.findByPhone(groomer.getPhone());
        if (existPhone.isPresent()) throw new RuntimeException("Phone 已存在");
        
        Optional<Groomer> existEmail = groomerRepository.findByEmail(groomer.getEmail());
        if (existEmail.isPresent()) throw new RuntimeException("Email 已存在");
        
        if (file != null && !file.isEmpty()) {
            String imageUrl = saveFile(file); 
            groomer.setPicture(imageUrl);     
        }
        
       
          
        Groomer savedGroomer = groomerRepository.save(groomer);
        log.info("新增美容師"+groomer.getGroomerName()+"成功");
        
        log.info("美容師 ID: {} 新增成功，開始自動產生 30 天排程...", savedGroomer.getGroomerId());
        dailyScheduleRepository.generateGroomerSchedules(
                savedGroomer.getGroomerId(),
                savedGroomer.getHiredate(), // 從入職日開始
                120,                         // 產生120天
                "09:00",                    // 預設上班時間
                "21:00",                    // 預設下班時間
                -1                          // 預設無每週公休
            );

            log.info("新增美容師 {} 成功且已建立排程", groomer.getGroomerName());
        
        return savedGroomer;
    }
	
	
	@Transactional
    public Groomer updateGroomerInfo(Integer id, Groomer inputGroomer, MultipartFile file) throws IOException {
        log.info("開始修改美容師: {id}", inputGroomer.getGroomerName());
        
        Groomer existing = groomerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的美容師"));

        if (!existing.getPhone().equals(inputGroomer.getPhone())) {
            Optional<Groomer> existPhone = groomerRepository.findByPhone(inputGroomer.getPhone());
            if (existPhone.isPresent()) {
                throw new RuntimeException("電話已經被其他美容師註冊過了");
            }
        }

        if (!existing.getEmail().equals(inputGroomer.getEmail())) {
            Optional<Groomer> existEmail = groomerRepository.findByEmail(inputGroomer.getEmail());
            if (existEmail.isPresent()) {
                throw new RuntimeException("Email 已經被其他美容師註冊過了");
            }
        }
        
        
        existing.setGroomerName(inputGroomer.getGroomerName());
        existing.setPhone(inputGroomer.getPhone());
        existing.setEmail(inputGroomer.getEmail());
        
        
        if (file != null && !file.isEmpty()) {
            String imageUrl = saveFile(file); 
            log.info("儲存圖片路徑進Groomer existing");
            existing.setPicture(imageUrl);     
        }
        
        Groomer save = groomerRepository.save(existing);
        log.info("儲存成功");
        
        return groomerRepository.save(save);
    }

    
	//照片儲存
    private String saveFile(MultipartFile file) throws IOException {
        Map params = ObjectUtils.asMap(
            "folder", "groomer_pictures",  
            "use_filename", true,
            "unique_filename", true
        );
        
        
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        log.info("上傳照片路徑到Cloudinary");
        
        return (String) uploadResult.get("secure_url");
    }
    
    public List<Groomer> searchGroomers(String name, LocalDate startDate, LocalDate endDate, Integer status) {
        Boolean isActive = null;
        if (status != null) {
            isActive = (status == 1);
        }
        return groomerRepository.complexSearch(name, startDate, endDate, isActive);
    }
    
    @Transactional
    public void updateGroomerStatus(Integer id, Boolean isActive) {
        Groomer groomer = groomerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的美容師"));
        
        LocalDate today = LocalDate.now();
        
        long appointmentCount = appointmentRepository.countActiveFutureAppointments(id, today);
        
        if(appointmentCount > 0) {
        	throw new RuntimeException("該美容師在 " + today + " 之後仍有 " + 
                    appointmentCount + " 筆未處理預約。請先手動取消再轉單後操作。");
        }else {
        	groomer.setIsActive(isActive);
            groomerRepository.save(groomer);
            
        	dailyScheduleRepository.deleteFutureSchedulesByGroomer(today, id);
            log.info("美容師 ID: {} 停權成功，已清除當日後的班表。", id);
        }   
    }


	// =========leave_record========
	public List<LeaveRecoredGroomerView> getAllLeaveRecords() {
		return leaveRecordGroomerViewRepository.findAll();
	}
	
	@Transactional
    public LeaveRecord createRecord(LeaveRecord leaveRecord) {
		
		Integer gId = leaveRecord.getGroomerId();
		LocalDate leaveDate = leaveRecord.getLeaveDate();
		
		
		if (!groomerRepository.existsById(gId)) {
	        throw new RuntimeException("無效的美容師 ID: " + gId);
	    }
		
		leaveRecord.setGroomerId(gId);
		if (leaveRecord.getIsActive() == null) {
            leaveRecord.setIsActive(true); 
        }
		
		LeaveRecord saverResult = leaveRecordRepository.save(leaveRecord);
		
		if(saverResult!=null) {
			
			long appointmentCount = appointmentRepository.countByGroomerIdAndAppointmentDate(gId, leaveDate);

			if (appointmentCount > 0) {
				throw new RuntimeException("該美容師於 " + leaveDate + " 尚有 " + appointmentCount + " 筆預約訂單，無法請假。請先取消或轉移訂單。");
			}

			
			leaveRecord.setGroomerId(gId);
			if (leaveRecord.getIsActive() == null) {
				leaveRecord.setIsActive(true);
			}

			
			LeaveRecord savedResult = leaveRecordRepository.save(leaveRecord);

			
			if (savedResult != null) {
				log.info("新增假單成功: GroomerId={}, Date={}", gId, leaveDate);
				
				dailyScheduleRepository.deleteByWorkDateAndGroomerId(leaveDate, gId);
				log.info("已刪除美容師: {} 日期 {} 的排程", gId, leaveDate);
			}
		}
			
        return saverResult;
    }

	@Transactional
    public LeaveRecord updateRecord(Integer leaveId, LeaveRecord inputRecord) {
        LeaveRecord existing = leaveRecordRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + leaveId + " 的請假單"));
        
        Boolean oldIsActive = existing.getIsActive();
        LocalDate targetDate = existing.getLeaveDate(); 
        Integer targetGroomerId = existing.getGroomerId();
   
        if (inputRecord.getIsActive() != null) {
            existing.setIsActive(inputRecord.getIsActive());
            
            if (oldIsActive && !inputRecord.getIsActive()) {
                log.info("駁回假單，正在恢復美容師 {} 於 {} 的排程", targetGroomerId, targetDate);
                
                
                dailyScheduleRepository.generateGroomerSchedules(
                    targetGroomerId, 
                    targetDate, 
                    1,          
                    "09:00",    
                    "21:00",   
                    -1          
                );
            }
        }
        

        return leaveRecordRepository.save(existing);
    }
	
	public List<LeaveRecoredGroomerView> searchLeaveRecords(
            Integer groomerId, 
            LocalDate leaveStart, LocalDate leaveEnd,
            LocalDate createStart, LocalDate createEnd) {
        
        
        LocalDateTime createStartTime = (createStart != null) ? createStart.atStartOfDay() : null;
        LocalDateTime createEndTime = (createEnd != null) ? createEnd.atTime(23, 59, 59) : null;

        return leaveRecordGroomerViewRepository.complexSearch(
                groomerId, 
                leaveStart, leaveEnd, 
                createStartTime, createEndTime);
    }

}
