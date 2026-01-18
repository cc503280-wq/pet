package com.pet.service.appointment;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pet.common.AppConstants;
import com.pet.dao.appointment.AppointmentRepository;
import com.pet.dao.appointment.DailyScheduleRepository;
import com.pet.dao.appointment.GroomerRepository;
import com.pet.dao.appointment.LeaveRecordGroomerViewRepository;
import com.pet.dao.appointment.LeaveRecordRepository;
import com.pet.model.appointment.Groomer;
import com.pet.model.appointment.LeaveRecord;
import com.pet.model.appointment.LeaveRecoredGroomerView;

import lombok.extern.slf4j.Slf4j;

/**
 * GroomerService: 處理美容師管理的核心業務邏輯
 * 負責：登入驗證、資料維護 (CRUD)、檔案上傳、以及複雜的請假審核邏輯
 */
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
	private LeaveRecordGroomerViewRepository leaveRecordGroomerViewRepository; // 用於查詢請假單 View
	
    @Autowired
	private Cloudinary cloudinary; // 圖片上傳服務
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	public List<Groomer> getAllGroomer() {
		return groomerRepository.findAll();
	}

	// 使用 BCrypt 加密驗證登入
	public Groomer groomerLogin(String email, String password) {
		Groomer groomer = groomerRepository.findByEmail(email).orElse(null);

		if (groomer == null) {
			log.info("登入失敗: email={} 帳號不存在", email);
			return null;
		}
		
		// 檢查帳號是否被停用 (isActive = false)
		if (groomer.getIsActive() == null || !groomer.getIsActive()) {
			log.info("登入失敗: email={} 帳號已被停用", email);
			throw new RuntimeException("ACCOUNT_DISABLED"); // 會被 Controller 捕獲並回傳 403
		}
		
		// 驗證密碼 (比對明碼與雜湊後的密碼)
		if (groomer.getPassword() != null && BCrypt.checkpw(password, groomer.getPassword())) {
			log.info("美容師 {} 登入成功", groomer.getGroomerId());
			return groomer;
		}
		
		log.info("登入失敗: email={} 密碼錯誤", email);
		return null; 
	}


	
	public Groomer getGroomerById(Integer id) {
		return groomerRepository.findById(id).orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的美容師"));
    }


    // 新增美容師 (包含密碼加密與圖片上傳)
	@Transactional
    public Groomer saveGroomerInfo(Groomer groomer, MultipartFile file) throws IOException {
        log.info("開始新增美容師: {}", groomer.getGroomerName());

        // 驗證 Email 和 Phone 唯一性
		validateUniqueGroomer(groomer.getPhone(), groomer.getEmail(), null);

		// 使用 BCrypt 加密密碼
		if (groomer.getPassword() != null && !groomer.getPassword().isEmpty()) {
			groomer.setPassword(BCrypt.hashpw(groomer.getPassword(), BCrypt.gensalt()));
		}
        
        // 如果有上傳檔案，呼叫 upload 存到 Cloudinary 並取得 URL
        if (file != null && !file.isEmpty()) {
            String imageUrl = saveFile(file); 
            groomer.setPicture(imageUrl);     
        }
        
        Groomer savedGroomer = groomerRepository.save(groomer);
		log.info("新增美容師 {} 成功", groomer.getGroomerName());
        
        // 自動產生未來 120 天的班表
        log.info("美容師 ID: {} 新增成功，開始自動產生 30 天排程...", savedGroomer.getGroomerId());
		dailyScheduleRepository.generateGroomerSchedules(
                savedGroomer.getGroomerId(), 
                savedGroomer.getHiredate(), // 從入職日開始
				120, // 產生 120 天
				AppConstants.DEFAULT_START_TIME, 
                AppConstants.DEFAULT_END_TIME, 
                -1 // 預設無固定每週公休
        );

        log.info("新增美容師 {} 成功且已建立排程", groomer.getGroomerName());
        
        return savedGroomer;
    }
	
    // 更新美容師資料
	@Transactional
    public Groomer updateGroomerInfo(Integer id, Groomer inputGroomer, MultipartFile file) throws IOException {
		log.info("開始修改美容師: {}", inputGroomer.getGroomerName());
        
		Groomer existing = groomerRepository.findById(id).orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的美容師"));

        // 檢查 Phone 是否變更且衝突
        if (!existing.getPhone().equals(inputGroomer.getPhone())) {
			validateUniquePhone(inputGroomer.getPhone());
        }

        // 檢查 Email 是否變更且衝突
        if (!existing.getEmail().equals(inputGroomer.getEmail())) {
			validateUniqueEmail(inputGroomer.getEmail());
        }
        
        existing.setGroomerName(inputGroomer.getGroomerName());
        existing.setPhone(inputGroomer.getPhone());
        existing.setEmail(inputGroomer.getEmail());
        
		// 如果有傳入新密碼，則重新加密更新
		if (inputGroomer.getPassword() != null && !inputGroomer.getPassword().isEmpty()) {
			String hashedPassword = BCrypt.hashpw(inputGroomer.getPassword(), BCrypt.gensalt());
			existing.setPassword(hashedPassword);
			log.info("密碼已加密更新");
		}
        
        if (file != null && !file.isEmpty()) {
            String imageUrl = saveFile(file); 
            log.info("儲存圖片路徑進Groomer existing");
            existing.setPicture(imageUrl);     
        }
        
        Groomer save = groomerRepository.save(existing);
        log.info("儲存成功");
        
		return save;
	}

    // 驗證輔助方法
	private void validateUniqueGroomer(String phone, String email, Integer currentId) {
		validateUniquePhone(phone);
		validateUniqueEmail(email);
	}

	private void validateUniquePhone(String phone) {
		if (groomerRepository.findByPhone(phone).isPresent()) {
			throw new RuntimeException("Phone 已存在");
		}
	}

	private void validateUniqueEmail(String email) {
		if (groomerRepository.findByEmail(email).isPresent()) {
			throw new RuntimeException("Email 已存在");
    }
	}
    
	// 圖片上傳邏輯
    private String saveFile(MultipartFile file) throws IOException {
		Map params = ObjectUtils.asMap("folder", "groomer_pictures", "use_filename", true, "unique_filename", true);
        
        // 使用 Cloudinary SDK 上傳
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        log.info("上傳照片路徑到Cloudinary");
        
        return (String) uploadResult.get("secure_url");
    }
    
    // 複合搜尋
    public List<Groomer> searchGroomers(String name, LocalDate startDate, LocalDate endDate, Integer status) {
        Boolean isActive = null;
        if (status != null) {
            isActive = (status == 1);
        }
        return groomerRepository.complexSearch(name, startDate, endDate, isActive);
    }
    
    // 更新美容師狀態 (含防呆檢查)
    @Transactional
    public void updateGroomerStatus(Integer id, Boolean isActive) {
		Groomer groomer = groomerRepository.findById(id).orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的美容師"));
        
        LocalDate today = LocalDate.now();
        
        // 檢查未來是否有未處理的預約
        long appointmentCount = appointmentRepository.countActiveFutureAppointments(id, today);
        
		if (appointmentCount > 0) {
			throw new RuntimeException("該美容師在 " + today + " 之後仍有 " + appointmentCount + " 筆未處理預約。請先手動取消再轉單後操作。");
		} else {
        	groomer.setIsActive(isActive);
            groomerRepository.save(groomer);
            
            // 如果是停權 (isActive = false)，則刪除未來的班表
			if (!isActive) {
        	dailyScheduleRepository.deleteFutureSchedulesByGroomer(today, id);
            log.info("美容師 ID: {} 停權成功，已清除當日後的班表。", id);
        }   
    }
	}

	// ========= 請假單 (Leave Record) 邏輯 ========
    
    // 取得所有請假紀錄
	public List<LeaveRecoredGroomerView> getAllLeaveRecords() {
		return leaveRecordGroomerViewRepository.findAll();
	}
	
	/**
     * 創建請假記錄
     * 1. 檢查日期起訖
     * 2. 檢查該時段是否有預約
     * 3. 檢查人力是否足夠 (至少保留一人)
     * 4. 若通過，則儲存假單，並將班表對應時段設為全滿 ('1')
     */
	@Transactional
    public LeaveRecord createRecord(LeaveRecord leaveRecord) {
		Integer gId = leaveRecord.getGroomerId();
		LocalDate start = leaveRecord.getStartDate();
		LocalDate end = leaveRecord.getEndDate();
		
		if (end.isBefore(start)) {
			throw new RuntimeException("結束日期不能早於開始日期");
	    }
		
		// 1. 檢查該時段內是否有「未取消」的預約
		long count = appointmentRepository.countByGroomerIdAndDateRange(gId, start, end);

		if (count > 0) {
			throw new RuntimeException("無法請假：該時段內已有 " + count + " 筆預約。");
		}
		
        // 2. 檢查請假期間內，是否每一天都至少還有一位其他美容師上班 (避免全店放空城)
        LocalDate checkDate = start;
        while (!checkDate.isAfter(end)) {
            // 計算當天排班且未請假的人數
            long workingCount = groomerRepository.countWorkingGroomers(checkDate);
            // 如果只剩 1 人 (也就是自己)，再請假就會變成 0 人
            if (workingCount <= 1) {
                throw new RuntimeException("無法請假：" + checkDate + " 必須至少留一位美容師上班。");
            }
            checkDate = checkDate.plusDays(1);
        }

		// 3. 儲存請假單
		LeaveRecord savedResult = leaveRecordRepository.save(leaveRecord);
			
		if (savedResult != null) {
			// 4. 更新該區間內的班表為全滿 '1' (表示當天不可預約)
            String fullSlots = "1".repeat(96); // 96個1 (24小時 * 4 slots/hr)
			LocalDate current = start;
			while (!current.isAfter(end)) {
				// 更新班表 time_slots
				dailyScheduleRepository.updateTimeSlots(current, gId, fullSlots);
				current = current.plusDays(1);
			}
		} else {
			throw new RuntimeException("新增假單失敗");
		}
			
		return savedResult;
    }

	// 修改請假記錄 (如：審核狀態變更)
	@Transactional
    public LeaveRecord updateRecord(Integer leaveId, LeaveRecord inputRecord) {
        LeaveRecord existing = leaveRecordRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + leaveId + " 的請假單"));
        
        Boolean oldIsActive = existing.getIsActive();
		LocalDate start = existing.getStartDate();
		LocalDate end = existing.getEndDate();
        Integer targetGroomerId = existing.getGroomerId();
   
        // 若狀態有變更 (例如：核准 -> 駁回)
        if (inputRecord.getIsActive() != null) {
            existing.setIsActive(inputRecord.getIsActive());
            
			// Caso 1: 從有效變成無效 (駁回/取消) -> 恢復班表
            if (oldIsActive && !inputRecord.getIsActive()) {
                // 先刪除該區間現有的全滿班表
                dailyScheduleRepository.deleteByGroomerIdAndDateRange(targetGroomerId, start, end);

				// 呼叫 SP 重新產生該區間的初始班表 (09:00~21:00)
				long days = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
                
				dailyScheduleRepository.generateGroomerSchedules(targetGroomerId, start, (int) days, "09:00",
						"21:00", -1 // WeeklyOffDay
				);
            }

            // Case 2: 從無效變成有效 (核准/重新申請) -> 設為全滿
            if (!oldIsActive && inputRecord.getIsActive()) {
                 // 1. 再次檢查人力 (避免人力不足時誤核准)
                 LocalDate checkDate = start;
                 while (!checkDate.isAfter(end)) {
                     long workingCount = groomerRepository.countWorkingGroomers(checkDate);
                     if (workingCount <= 1) {
                         throw new RuntimeException("無法核准：" + checkDate + " 必須至少留一位美容師上班。");
                     }
                     checkDate = checkDate.plusDays(1);
                 }
                 
                 // 2. 更新班表為 96 個 1 (鎖定時段)
                 String fullSlots = "1".repeat(96);
                 LocalDate current = start;
                 while (!current.isAfter(end)) {
                     dailyScheduleRepository.updateTimeSlots(current, targetGroomerId, fullSlots);
                     current = current.plusDays(1);
                 }
            }
        }
        
		// 允許修改原因
		if (inputRecord.getReason() != null) {
			existing.setReason(inputRecord.getReason());
		}

        return leaveRecordRepository.save(existing);
    }
	
	// 批次請假 (轉發給 createRecord)
	@Transactional
	public void createLeaveRecordsBatch(com.pet.dto.appointment.LeaveRequestDto request) {
		LeaveRecord record = new LeaveRecord();
		record.setGroomerId(request.getGroomerId());
		record.setStartDate(request.getStartDate());
		record.setEndDate(request.getEndDate());
		record.setReason(request.getReason());
		record.setIsActive(true);

		// 直接呼叫 createRecord
		createRecord(record);
	}

	public List<LeaveRecoredGroomerView> searchLeaveRecords(Integer groomerId, LocalDate leaveStart, LocalDate leaveEnd,
            LocalDate createStart, LocalDate createEnd) {
        
        LocalDateTime createStartTime = (createStart != null) ? createStart.atStartOfDay() : null;
        LocalDateTime createEndTime = (createEnd != null) ? createEnd.atTime(23, 59, 59) : null;

		return leaveRecordGroomerViewRepository.complexSearch(groomerId, leaveStart, leaveEnd, createStartTime,
				createEndTime);
    }

}
