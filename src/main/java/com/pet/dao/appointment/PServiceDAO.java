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

import com.pet.model.appointment.PetServiceBean;


public class PServiceDAO {

	protected Connection getConnection() throws NamingException, SQLException {

		Context context = new InitialContext();
		DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
		return ds.getConnection();
	}

	public int insertPService(PetServiceBean psb) throws SQLException, NamingException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int rowsAffected = 0;

		try {
			// 1. 取得連線 (DAO 自己的職責)
			conn = getConnection();

			String SQL = "INSERT INTO Service ( service_name, price, duration_minutes) VALUES(?, ?, ?)";
			;

			pstmt = conn.prepareStatement(SQL);


			pstmt.setString(1, psb.getServiceName());
			pstmt.setInt(2, psb.getPrice());
			pstmt.setInt(3, psb.getDurationMinutes());
			
			
			rowsAffected = pstmt.executeUpdate();

			return rowsAffected;
		} catch (SQLException | NamingException e) {
			e.printStackTrace();

			throw e;
		} finally {
			// 5. 關閉資源
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


	public List<PetServiceBean> SearchAll() throws SQLException, NamingException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "SELECT * FROM Service";
		List<PetServiceBean> psbs = new ArrayList<>();
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);

			rs = pstmt.executeQuery();

			PetServiceBean psb = null;
			while (rs.next()) {

				psb = new PetServiceBean();
				psb.setServiceId(rs.getInt("service_id"));
				psb.setServiceName(rs.getString("service_name"));
				psb.setPrice(rs.getInt("price"));
				psb.setDurationMinutes(rs.getInt("duration_minutes"));

				psbs.add(psb);

			}

			pstmt.close();
			rs.close();

			return psbs;

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

	public int DelByPServiceId(Integer serviceId) throws SQLException, NamingException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "DELETE FROM Service WHERE service_id=?";
		int rowsAffected = 0;

		try {

			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, serviceId);
			rowsAffected = pstmt.executeUpdate();

			return rowsAffected;

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		} finally {
			// 確保關閉資源
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

	public PetServiceBean SearchByPserviceId(Integer serviceId) throws SQLException, NamingException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PetServiceBean psb = null;
		String SQL = "SELECT * From Service WHERE service_id= ?";

		try {

			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			
			pstmt.setInt(1, serviceId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				psb = new PetServiceBean();
				psb.setServiceId(rs.getInt("service_id"));
				psb.setServiceName(rs.getString("service_name"));
				psb.setPrice(rs.getInt("price"));
				psb.setDurationMinutes(rs.getInt("duration_minutes"));
			
			}

			return psb;
		} catch (SQLException | NamingException e) {
			e.printStackTrace();
			throw e; // 將例外拋出給 Controller 處理
		} finally {
			// 4. 關閉資源
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

	public int UpdateByPServiceId(PetServiceBean psb) throws SQLException, NamingException, ParseException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int rowsAffected = 0;

		String SQL = "UPDATE service SET service_name=?, price=?, duration_minutes=? WHERE service_id=?";

		try {
			conn = getConnection();

			pstmt = conn.prepareStatement(SQL);

			
			pstmt.setString(1, psb.getServiceName());
			pstmt.setInt(2, psb.getPrice());
			pstmt.setInt(3, psb.getDurationMinutes());
			pstmt.setInt(4, psb.getServiceId());
			
			rowsAffected = pstmt.executeUpdate();

			return rowsAffected;

		} catch (SQLException | NamingException e) {
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



}
