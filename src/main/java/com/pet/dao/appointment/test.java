package com.pet.dao.appointment;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.pet.model.appointment.EmployeeScheduleOverviewDTO;
import com.pet.utils.HibernateUtil;

public class test {

    public static void main(String[] args) {
        
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.getCurrentSession();
        
        try {
            session.beginTransaction();
            
           
            ScheduleQueryDAO dao = new ScheduleQueryDAO(session);
            
            // 2. 設定測試參數
            // 請確保資料庫裡這一天有 WorkSlot 資料，以及一些測試用的 Appointment 或 ScheduleBlock
            LocalDate targetDate = LocalDate.of(2025, 12, 20); // ★ 請改成你 DB 有資料的日期
            Integer testEmployeeId = 1; // 測試特定員工，若要查全部填 null
            
            System.out.println(">>> 開始測試每日排程概覽查詢...");
            System.out.println("查詢日期: " + targetDate);
            System.out.println("篩選員工ID: " + (testEmployeeId == null ? "全部" : testEmployeeId));
            
            // 3. 執行查詢
            List<EmployeeScheduleOverviewDTO> result = dao.findDailyScheduleOverview(targetDate, testEmployeeId);
            
            // 4. 驗證結果
            if (result != null && !result.isEmpty()) {
                System.out.println("---------------------------------------------------------------------------------");
                System.out.printf("%-6s | %-10s | %-15s | %-10s | %-10s%n", 
                        "SlotID", "員工姓名", "時間範圍", "狀態", "詳情");
                System.out.println("---------------------------------------------------------------------------------");
                
                for (EmployeeScheduleOverviewDTO dto : result) {
                    String timeRange = dto.getStartTime() + " ~ " + dto.getEndTime();
                    String status = dto.getSlotStatus(); // 可預約 / 已預約 / 請假原因
                    String detail = dto.getDetailInfo() == null ? "" : dto.getDetailInfo();
                    
                    System.out.printf("%-6d | %-10s | %-15s | %-10s | %-10s%n", 
                            dto.getSlot_id(), 
                            dto.getEmployeeName(), 
                            timeRange, 
                            status, 
                            detail);
                }
                System.out.println("---------------------------------------------------------------------------------");
                System.out.println("總筆數: " + result.size());
                
            } else {
                System.out.println(">>> 查無資料 (Result is empty)");
                System.out.println("可能原因：");
                System.out.println("1. WorkSlot 表是空的 (Cross Join 需要有時段資料)");
                System.out.println("2. Employee 表沒有 Active 的員工");
                System.out.println("3. DTO 建構子不匹配");
            }
            
            session.getTransaction().commit();
            
        } catch (Exception e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSessionFactory();
        }
    }
}