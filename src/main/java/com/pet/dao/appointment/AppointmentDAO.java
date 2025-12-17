package com.pet.dao.appointment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.pet.model.appoinment.AppointmentBean;
import com.pet.model.appoinment.EmpBean;
import com.pet.model.appoinment.GetAllAppointmentDTO;

public class AppointmentDAO {

	protected Connection getConnection() throws NamingException, SQLException {

		Context context = new InitialContext();
		DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
		return ds.getConnection();
	}

	public int insertAppointment(AppointmentBean app) throws SQLException, NamingException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int rowsAffected = 0;

		try {
			
			conn = getConnection();

			String SQL = "INSERT INTO Appointment ("
					+ "pet_id, "
					+ "service_id, "
					+ "employee_id, "
					+ "appointment_date, "
					+ "slot_id, "
					+ "notes,"
					+ "appointment_status,"
					+ "total_price) "
					+ "VALUES(?, ?, ?, ?, ?, ?,?,?)";
	

			pstmt = conn.prepareStatement(SQL);

			
			pstmt.setInt(1, app.getPetId());
			pstmt.setInt(2, app.getServiceId());
			pstmt.setInt(3, app.getEmployeeId());
			pstmt.setDate(4, app.getAppointmentDate());
			pstmt.setInt(5, app.getSlotId());
			pstmt.setString(6, app.getNotes());
			pstmt.setString(7, app.getAppointmentStatus());
			pstmt.setInt(8, app.getTotalPrice());

			rowsAffected = pstmt.executeUpdate();
			

			return rowsAffected;
		} catch (SQLException | NamingException e) {
			e.printStackTrace();

			throw e;
		} finally {
			
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public int DelByAppointmentId(Integer appointmentId) throws SQLException, NamingException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "DELETE FROM appointment WHERE appointment_id=?";
		int rowsAffected = 0;

		try {

			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, appointmentId);
			rowsAffected = pstmt.executeUpdate();

			return rowsAffected;

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		} finally {
			
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

		}

	}
	
	public int SoftDelByAppointmentId(Integer appointmentId) throws SQLException, NamingException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "UPDATE appointment SET appointment_status = '已取消' ,pay_status = '已退款', updated_at = GETDATE() WHERE appointment_id = ?;";
				
		int rowsAffected = 0;

		try {

			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, appointmentId);
			rowsAffected = pstmt.executeUpdate();

			return rowsAffected;

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

		}

	}

	public GetAllAppointmentDTO SearchByAppointmentId(Integer appointmentId) throws SQLException, NamingException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		GetAllAppointmentDTO dto = null;
		String SQL = "SELECT"
				+ "    a.appointment_id,"
				+ "    m.member_id, "
				+ "    m.name AS member_name,"
				+ "    a.pet_id, "
				+ "    p.pet_name AS pet_name,"
				+ "    a.service_id,"
				+ "    s.service_name AS service_name,"
				+ "    s.duration_minutes,"
				+ "    a.employee_id,"
				+ "    e.ename AS employee_name, "
				+ "    a.appointment_date,"
				+ "    ws.slot_id,"
				+ "    ws.start_time, "
				+ "    ws.end_time,"
				+ "    a.notes, "
				+ "    a.total_price,"
				+ "    a.appointment_status, "
				+ "    a.pay_status, "
				+ "    a.Rating AS rating,"
				+ "    a.comment, "
				+ "    a.reply, "
				+ "    a.updated_at "
				+ "FROM appointment a "
				+ "    JOIN member_pets p ON a.pet_id = p.pet_id"
				+ "    JOIN members m ON p.member_id = m.member_id"
				+ "    JOIN service s ON a.service_id = s.service_id"
				+ "    JOIN employee e ON a.employee_id = e.employee_id"
				+ "    JOIN work_slot ws ON a.slot_id = ws.slot_id "
				+ "WHERE a.appointment_id = ?";

		try {

			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);

			pstmt.setInt(1, appointmentId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new GetAllAppointmentDTO();
				dto.setAppointmentId(rs.getInt("appointment_id"));
				dto.setMemberId(rs.getInt("member_id"));
				dto.setMemberName(rs.getString("member_name"));
				dto.setPetId(rs.getInt("pet_id"));
				dto.setPetName(rs.getString("pet_name"));
				dto.setServiceId(rs.getInt("service_id"));
				dto.setServiceName(rs.getString("service_name"));
				dto.setEmployeeId(rs.getInt("employee_id"));
				dto.setEmployeeName(rs.getString("employee_name"));
				dto.setAppointmentDate(rs.getDate("appointment_date"));
				dto.setStartTime(rs.getTime("start_time"));
				dto.setEndTime(rs.getTime("end_time"));	
				dto.setNotes(rs.getString("notes"));
				dto.setPrice(rs.getInt("total_price"));
				dto.setAppointmentStatus(rs.getString("appointment_status"));
				dto.setPayStatus(rs.getString("pay_status"));
				dto.setRating(rs.getInt("rating"));
				dto.setComment(rs.getString("comment"));
				dto.setReply(rs.getString("reply"));
				dto.setUpdateTime(rs.getDate("updated_at"));
				dto.setSlotId(rs.getInt("slot_id"));
				dto.setDurationMinutes(rs.getInt("duration_minutes"));
			}
					

			return dto;
		} catch (SQLException | NamingException e) {
			e.printStackTrace();
			throw e; 
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	public int updateAppointment(AppointmentBean app) throws SQLException, NamingException {
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    int rowsAffected = 0;

	    
	    String SQL = "UPDATE appointment SET "
	            + "pet_id=?, service_id=?, employee_id=?, appointment_date=?, slot_id=?, "
	            + "notes=?, appointment_status=?, pay_status=?, total_price=?, "
	            + "Rating=?, comment=?, reply=?, updated_at=GETDATE() "
	            + "WHERE appointment_id=?";

	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);

	        pstmt.setInt(1, app.getPetId());
	        pstmt.setInt(2, app.getServiceId());
	        pstmt.setInt(3, app.getEmployeeId());
	        pstmt.setDate(4, app.getAppointmentDate());
	        pstmt.setInt(5, app.getSlotId());
	        pstmt.setString(6, app.getNotes());
	        pstmt.setString(7, app.getAppointmentStatus());
	        pstmt.setString(8, app.getPayStatus());
	        pstmt.setInt(9, app.getTotalPrice());

	        if (app.getRating() != null && app.getRating() > 0) {
	            pstmt.setInt(10, app.getRating());
	        } else {
	            pstmt.setNull(10, java.sql.Types.TINYINT);
	        }
	        
	        pstmt.setString(11, app.getComment());
	        pstmt.setString(12, app.getReply());
	        pstmt.setInt(13, app.getAppointmentId());

	        rowsAffected = pstmt.executeUpdate();
	        
	        return rowsAffected;

	    } catch (SQLException | NamingException e) {
	        e.printStackTrace();
	        throw e; 
	    } finally {
	        if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
	        if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
	    }
	}

	
	public List<GetAllAppointmentDTO> getAllAppointment()throws NamingException, SQLException{ 
		
		List<GetAllAppointmentDTO> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT"
				+ "    a.appointment_id,"
				+ "    m.member_id, "
				+ "    m.name,"
				+ "    a.pet_id, "
				+ "    p.pet_name,"
				+ "    a.service_id,"
				+ "    s.service_name,"
				+"     s.duration_minutes, "
				+ "    a.employee_id,"
				+ "    e.ename, "
				+ "    a.appointment_date,"
				+ "    ws.slot_id, "
				+ "    ws.start_time, "
				+ "    ws.end_time,"
				+ "    a.notes, "
				+ "    a.total_price,"
				+ "    a.appointment_status, "
				+ "    a.pay_status, "
				+ "    a.Rating,"
				+ "    a.comment, "
				+ "    a.reply, "
				+ "    a.updated_at "
				+ "FROM appointment a "
				+ "    JOIN member_pets p ON a.pet_id = p.pet_id"
				+ "    JOIN members m ON p.member_id = m.member_id"
				+ "    JOIN service s ON a.service_id = s.service_id"
				+ "    JOIN employee e ON a.employee_id = e.employee_id"
				+ "    JOIN work_slot ws ON a.slot_id = ws.slot_id "
				+ "ORDER BY "
				+ "    a.appointment_date ASC,"
				+ "    a.appointment_id DESC;";
		
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			GetAllAppointmentDTO dto = new GetAllAppointmentDTO();
			while (rs.next()) {

				dto = new GetAllAppointmentDTO(
				
				rs.getInt("appointment_id"),
                rs.getInt("member_id"),
                rs.getString("name"),
                rs.getInt("pet_id"),
                rs.getString("pet_name"),
                rs.getInt("service_id"),
                rs.getString("service_name"),
                rs.getInt("employee_id"),
                rs.getString("ename"),
                rs.getDate("appointment_date"), // java.sql.Date
                rs.getTime("start_time"),       // java.sql.Time
                rs.getTime("end_time"),         // java.sql.Time
                rs.getString("notes"),
                rs.getInt("total_price"),
                rs.getString("appointment_status"),
                rs.getString("pay_status"),     // 注意：如果資料庫是 NULL，getString 會回傳 null
                rs.getInt("Rating"),            // 注意：如果 rating 是 NULL，getInt 會回傳 0
                rs.getString("comment"),
                rs.getString("reply"),
                rs.getTimestamp("updated_at"),   // 建議用 Timestamp 拿取完整時間，它相容於 java.util.Date
                rs.getInt("slot_id"),
                rs.getInt("duration_minutes")
            );
            
            list.add(dto);
        }
				

			

			pstmt.close();
			rs.close();

			return list;

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		} finally {

			try {

				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


		
	}
	
public List<GetAllAppointmentDTO> getFuzzySearchByName(String memberName) throws NamingException, SQLException {
		
		
		String SQL =  "SELECT"
				+ "    a.appointment_id,"
				+ "    m.member_id, "
				+ "    m.name,"
				+ "    a.pet_id, "
				+ "    p.pet_name,"
				+ "    a.service_id,"
				+ "    s.service_name,"
				+"     s.duration_minutes, "
				+ "    a.employee_id,"
				+ "    e.ename, "
				+ "    a.appointment_date,"
				+ "    ws.slot_id, "
				+ "    ws.start_time, "
				+ "    ws.end_time,"
				+ "    a.notes, "
				+ "    a.total_price,"
				+ "    a.appointment_status, "
				+ "    a.pay_status, "
				+ "    a.Rating,"
				+ "    a.comment, "
				+ "    a.reply, "
				+ "    a.updated_at "
				+ "FROM appointment a "
				+ "    JOIN member_pets p ON a.pet_id = p.pet_id"
				+ "    JOIN members m ON p.member_id = m.member_id"
				+ "    JOIN service s ON a.service_id = s.service_id"
				+ "    JOIN employee e ON a.employee_id = e.employee_id"
				+ "    JOIN work_slot ws ON a.slot_id = ws.slot_id "
				+ "WHERE m.name Like ?"
				+ "ORDER BY "
				+ "    a.appointment_date ASC,"
				+ "    a.appointment_id DESC;"; // 依照日期排序
		List<GetAllAppointmentDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try(Connection conn = getConnection()){
				
		  
			pstmt = conn.prepareStatement(SQL); 
			pstmt.setString(1, "%" + memberName + "%");
			rs = pstmt.executeQuery();	
			EmpBean emp =null;
				while (rs.next()) {
					
				GetAllAppointmentDTO dto = new GetAllAppointmentDTO(
							
							rs.getInt("appointment_id"),
			                rs.getInt("member_id"),
			                rs.getString("name"),
			                rs.getInt("pet_id"),
			                rs.getString("pet_name"),
			                rs.getInt("service_id"),
			                rs.getString("service_name"),
			                rs.getInt("employee_id"),
			                rs.getString("ename"),
			                rs.getDate("appointment_date"), 
			                rs.getTime("start_time"),       
			                rs.getTime("end_time"),        
			                rs.getString("notes"),
			                rs.getInt("total_price"),
			                rs.getString("appointment_status"),
			                rs.getString("pay_status"),    
			                rs.getInt("Rating"),            
			                rs.getString("comment"),
			                rs.getString("reply"),
			                rs.getTimestamp("updated_at"),   
			                rs.getInt("slot_id"),
			                rs.getInt("duration_minutes")
			            );
			            
			            list.add(dto);
			        }
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
			
		}catch(NamingException e) {
			e.printStackTrace();
			throw e;
		}finally {
			pstmt.close();
			rs.close();
		}
		return list;

 }
}
