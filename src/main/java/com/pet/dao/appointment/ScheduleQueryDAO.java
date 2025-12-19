package com.pet.dao.appointment;

import java.sql.Connection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.Session;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.appointment.EmployeeScheduleOverviewDTO;



public class ScheduleQueryDAO {
	
	private Session session;

	public ScheduleQueryDAO(Session session) {
		this.session = session;
	}
	
	public List<EmployeeScheduleOverviewDTO> findDailyScheduleOverview(LocalDate targetDate,Integer employeeIdFilter)
			throws NamingException, SQLException{
		
		Integer finalFilterId = (employeeIdFilter == null || employeeIdFilter == 0) ? null : employeeIdFilter;
		
		String hql = "SELECT new com.pet.model.appointment.EmployeeScheduleOverviewDTO(" +
	            "   ws.slotId, " +
	            "   e.employeeId, " +
	            "   e.ename, " +
	            "   ws.startTime, " +
	            "   ws.endTime, " +
	            
	            // ★ 關鍵改動 1: 使用 COALESCE + 子查詢來決定狀態
	            // 邏輯: 優先找預約(有就回傳'已預約') -> 沒預約找排假(有就回傳理由) -> 都沒有就回傳'可預約'
	            "   COALESCE(" +
	            "       (SELECT '已預約' FROM Appointment a WHERE a.employee = e AND a.workSlot = ws AND a.appointmentDate = :targetDate AND a.appointmentStatus IN ('預約確認', '進行中', '已完成')), " +
	            "       (SELECT sb.reason FROM ScheduleBlock sb WHERE sb.employee = e AND sb.workSlot = ws AND sb.blockDate = :targetDate), " +
	            "       '可預約' " +
	            "   ), " +
	            
	            // ★ 關鍵改動 2: 詳情欄位也用子查詢
	            "   (SELECT '被預約' FROM Appointment a WHERE a.employee = e AND a.workSlot = ws AND a.appointmentDate = :targetDate AND a.appointmentStatus IN ('預約確認', '進行中', '已完成')) " +
	            ") " +
	            
	            // ★ 這裡只留 Cross Join，乾淨俐落，不寫會報錯的 Left Join
	            "FROM Employee e, WorkSlot ws " +
	            
	            "WHERE e.isActive = true " +
	            "AND (:employeeIdFilter IS NULL OR e.employeeId = :employeeIdFilter) " +
	            "ORDER BY e.employeeId, ws.slotId";
		
		try {
	        return session.createQuery(hql, EmployeeScheduleOverviewDTO.class)
	                      .setParameter("targetDate", targetDate)
	                      .setParameter("employeeIdFilter", finalFilterId)
	                      .getResultList();
	                      
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ArrayList<>(); 
	    }
	}
	
	public List<Map<String, Object>> generateTabulatorSchedule(Integer employeeIdFilter, String startDateStr) 
	        throws NamingException, SQLException {

	    
	    LocalDate startDate = LocalDate.parse(startDateStr); 
	    List<LocalDate> weekDates = new ArrayList<>();
	    for (int i = 0; i < 7; i++) {
	        weekDates.add(startDate.plusDays(i)); 
	    }
	    System.out.println("一周日期"+weekDates);

	
	    Map<String, Map<String, Object>> combinedSchedule = new LinkedHashMap<>();

	    for (LocalDate date : weekDates) {

           
	        List<EmployeeScheduleOverviewDTO> dailyList = findDailyScheduleOverview(date, employeeIdFilter); 
	        String dateField = date.toString();

	        
	        Set<String> keysFoundToday = new HashSet<>();

	        for (EmployeeScheduleOverviewDTO dto : dailyList) {

	            String slotKey = dto.getEmployeeId() + "-" + dto.getStartTime();

                
	            Map<String, Object> slotRow = combinedSchedule.computeIfAbsent(slotKey, k -> {
	               
	                Map<String, Object> row = new LinkedHashMap<>(); 
	                row.put("employeeId", dto.getEmployeeId());
	                row.put("employeeName", dto.getEmployeeName());
	                
	                row.put("slotId", dto.getSlot_id());
	                row.put("startTime", dto.getStartTime().toString());
	                row.put("endTime", dto.getEndTime().toString());
	                System.out.println("row"+row);
	                return row;
	            });
	            
	            
	            slotRow.put(dateField, dto.getSlotStatus());
	            keysFoundToday.add(slotKey);
	        }

	       
	        for (String slotKey : combinedSchedule.keySet()) {
	            if (!keysFoundToday.contains(slotKey)) {
	              
                    if (!combinedSchedule.get(slotKey).containsKey(dateField)) {
                        combinedSchedule.get(slotKey).put(dateField, "—");
                    }
	            }
	        }
	    }
        
       
	    System.out.println("合併排程combinedSchedule: "+new ArrayList<>(combinedSchedule.values()));
	    return new ArrayList<>(combinedSchedule.values());
	}
	

}
