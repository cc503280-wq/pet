package dao;

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
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.EmployeeScheduleOverviewDTO;

public class ScheduleQueryDAO {
	
	protected Connection getConnection() throws NamingException, SQLException {
        
        Context context = new InitialContext();
        DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
        return ds.getConnection();
    }
	
	public List<EmployeeScheduleOverviewDTO> findDailyScheduleOverview(LocalDate targetDate,Integer employeeIdFilter)
			throws NamingException, SQLException{
		
		String sql =
		        "SELECT " +
		        "WS.slot_id, "+
		        "E.employee_id, E.ename AS employeeName, WS.start_time, WS.end_time, " +
		        "CASE " +
		        "WHEN A.appointment_id IS NOT NULL THEN N'已預約' " +
		        "WHEN SB.block_id IS NOT NULL THEN SB.reason " + 
		        "ELSE N'可預約' END AS slotStatus, " +
		        "CASE WHEN A.appointment_id IS NOT NULL THEN N'被預約' ELSE NULL END AS detailInfo " +
		        "FROM work_slot WS " +
		        "CROSS JOIN employee E " +
		        "LEFT JOIN appointment A ON A.employee_id = E.employee_id AND A.appointment_date = ? AND A.slot_id = WS.slot_id AND A.appointment_status in (N'預約確認', N'進行中', N'已完成') " + // **參數 4: TargetDate**
		        "LEFT JOIN schedule_block SB ON SB.employee_id = E.employee_id AND SB.block_date = ? AND SB.slot_id = WS.slot_id " + // **參數 5: TargetDate**
		        "WHERE (? IS NULL OR ? = 0 OR E.employee_id = ?) " + 
		        "AND E.is_active != 0 " +
		        "ORDER BY E.employee_id, WS.slot_id";
		
		List<EmployeeScheduleOverviewDTO> scheduleList = new ArrayList<>();
		
		Integer filterId = (employeeIdFilter == null || employeeIdFilter == 0) ? null : employeeIdFilter;
		
		try (Connection conn = getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            
	            pstmt.setDate(1, Date.valueOf(targetDate)); 
	            pstmt.setDate(2, Date.valueOf(targetDate)); 
	            
	           
	            if (filterId == null) {
	                pstmt.setNull(3, Types.INTEGER);
	                pstmt.setNull(4, Types.INTEGER);
	                pstmt.setNull(5, Types.INTEGER);
	            } else {
	                pstmt.setInt(3, filterId);
	                pstmt.setInt(4, filterId);
	                pstmt.setInt(5, filterId);
	            }


	            try (ResultSet rs = pstmt.executeQuery()) {
	                while (rs.next()) {
	                    EmployeeScheduleOverviewDTO dto = new EmployeeScheduleOverviewDTO();
	                    dto.setSlot_id(rs.getInt("slot_id"));
	                    dto.setEmployeeId(rs.getInt("employee_id"));
	                    dto.setEmployeeName(rs.getString("employeeName"));
	                    dto.setStartTime(rs.getTime("start_time").toLocalTime());
	                    dto.setEndTime(rs.getTime("end_time").toLocalTime());
	                    dto.setSlotStatus(rs.getString("slotStatus"));
	                    dto.setDetailInfo(rs.getString("detailInfo"));
	                    
	                    scheduleList.add(dto);
	                }
	                System.out.println("單日排成"+scheduleList);
	              
	            }

	        } catch (SQLException e) {
	            System.err.println("查詢每日排程概覽失敗: " + e.getMessage());
	            throw e;
	        }

	        return scheduleList;
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
