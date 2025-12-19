package com.pet.dao.appointment;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pet.model.appointment.GetAllAppointmentDTO;

public class AppointmentDAOTest {

	private SessionFactory factory;
	private Session session;
	private AppointmentDAO dao;

	@BeforeEach
	public void setUp() {
		// 重要：這裡讀取我們剛剛建立的「測試專用設定檔」
		// 這樣就不會去讀原本依賴 Tomcat JNDI 的設定
		Configuration config = new Configuration().configure("hibernate-test.cfg.xml");
		factory = config.buildSessionFactory();
		session = factory.openSession();
		
		// 初始化 DAO
		dao = new AppointmentDAO(session);
	}

	@AfterEach
	public void tearDown() {
		if (session != null) session.close();
		if (factory != null) factory.close();
	}

	@Test
	public void testFindAllAppointmentDTOs() {
		System.out.println("====== 測試查詢所有預約單 ======");
		
		List<GetAllAppointmentDTO> list = dao.findAllAppointmentDTOs();
		
		if (list == null || list.isEmpty()) {
			System.out.println("查詢結果為空！請確認資料庫是否有資料。");
		} else {
			System.out.println("成功查詢到 " + list.size() + " 筆資料：");
			for (GetAllAppointmentDTO dto : list) {
				System.out.println("訂單ID: " + dto.getAppointmentId() + 
						           ", 會員: " + dto.getMemberName() + 
						           ", 服務: " + dto.getServiceName());
			}
		}
		System.out.println("==============================\n");
	}

	@Test
	public void testSearchByAppointmentId() {
		System.out.println("====== 測試依 ID 查詢 ======");
		
		// 請確保資料庫裡真的有 ID 為 1 的資料，或改成存在的 ID
		int testId = 1; 
		GetAllAppointmentDTO dto = dao.searchByAppointmentId(testId);
		
		if (dto != null) {
			System.out.println("找到資料！");
			System.out.println("ID: " + dto.getAppointmentId());
			System.out.println("備註: " + dto.getNotes());
		} else {
			System.out.println("找不到 ID 為 " + testId + " 的資料。");
		}
		System.out.println("==========================\n");
	}
}