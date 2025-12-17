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

import com.pet.model.appoinment.EmpBean;

public class EmpDAO {

	
	protected Connection getConnection() throws NamingException, SQLException {
        
        Context context = new InitialContext();
        DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
        return ds.getConnection();
    }

	public int insertEmp(EmpBean emp) throws SQLException, NamingException {
			
			Connection conn = null;
	        PreparedStatement pstmt = null;
	        int rowsAffected = 0;
	        
	        try {
	            
	            conn = getConnection(); 
	            String SQL = "INSERT INTO employee (ename, phone, email, hiredate, profile_photo, is_active) VALUES(?, ?, ?, ?, ?, ?)" ; ;
	            
	            pstmt = conn.prepareStatement(SQL);
	            
		        pstmt.setString(1, emp.getEname());
		        pstmt.setString(2, emp.getPhone());
		        pstmt.setString(3, emp.getEmail());
		        
		        if (emp.getHiredate() != null) {
		        	pstmt.setDate(4, emp.getHiredate()); 
		        } else {
		            pstmt.setNull(4, java.sql.Types.DATE);
		        }
		        
		        
		        pstmt.setString(5, emp.getProfilePhoto());
		        pstmt.setBoolean(6, emp.getIsActive());
	            
	
				rowsAffected = pstmt.executeUpdate(); 
				
	            return rowsAffected; 
			} catch (SQLException | NamingException e) {
				e.printStackTrace();

	            throw e; 
			} finally {
				
				if (pstmt != null) {
					try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
				}
				if (conn != null) {
					try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
				}
			}
	}
	
	public EmpBean findByPrimaryKey(Integer employeeId) throws SQLException, NamingException {
	    
	    final String SQL = "SELECT employee_id, ename, phone, email, hiredate, profile_photo, is_active FROM employee WHERE employee_id = ?";
	    
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    java.sql.ResultSet rs = null;
	    EmpBean emp = null;

	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setInt(1, employeeId);
	        rs = pstmt.executeQuery();

	        if (rs.next()) {
	            emp = new EmpBean();
	           
	            emp.setEmployeeId(rs.getInt("employee_id")); 
	            
	         
	            emp.setEname(rs.getString("ename"));
	            emp.setPhone(rs.getString("phone"));
	            emp.setEmail(rs.getString("email"));
	            Date sqlDate = rs.getDate("hiredate");
	            emp.setHiredate(sqlDate); 
	        
	            
	          
	            emp.setProfilePhoto(rs.getString("profile_photo"));
	            
	           
	            emp.setIsActive(rs.getBoolean("is_active")); 
	        }
	    } catch (SQLException | NamingException e) {
	        e.printStackTrace();
	        throw e;
	    } finally {
	        if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
	        if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
	        if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
	    }
	    return emp;
	}
	
	public List<EmpBean> SearchAll()throws SQLException, NamingException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "SELECT * FROM employee";
		List<EmpBean> emps = new ArrayList<>();
		try {
			conn = getConnection();		
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			
			EmpBean emp = null;
			while (rs.next()) {
				
				emp=new EmpBean();
				emp.setEmployeeId(rs.getInt("employee_id"));
				emp.setEname(rs.getString("ename"));
				emp.setPhone(rs.getString("phone"));
				emp.setEmail(rs.getString("email"));
				Date sqlDate = rs.getDate("hiredate");
				emp.setHiredate(sqlDate);
				emp.setIsActive(rs.getBoolean("is_active"));
				emps.add(emp);
				
			}
			
			pstmt.close();
			rs.close();
			
			return emps;
			

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		}finally {
			
			try {
				
				
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public List<EmpBean> getAllActiveEmp()throws SQLException, NamingException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "SELECT * FROM employee WHERE is_Active = 1";
		List<EmpBean> emps = new ArrayList<>();
		try {
			conn = getConnection();		
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			
			EmpBean emp = null;
			while (rs.next()) {
				
				emp=new EmpBean();
				emp.setEmployeeId(rs.getInt("employee_id"));
				emp.setEname(rs.getString("ename"));
				emp.setPhone(rs.getString("phone"));
				emp.setEmail(rs.getString("email"));
				Date sqlDate = rs.getDate("hiredate");
				emp.setHiredate(sqlDate);
				emp.setIsActive(rs.getBoolean("is_active"));
				emps.add(emp);
				
			}
			
			pstmt.close();
			rs.close();
			
			return emps;
			

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		}finally {
			
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public int DelByEmpId(Integer employeeId) throws SQLException, NamingException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "DELETE FROM employee WHERE employee_id=?";
		int rowsAffected = 0;
		
		try {
			
			
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, employeeId);
			rowsAffected = pstmt.executeUpdate();
			
			
			return rowsAffected;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		}finally {
	        // 確保關閉資源
	        if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
	        if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
	    
		}
		
		
		
	}
	
	public EmpBean SearchByEmpId(String empno) throws SQLException, NamingException{
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;        
        EmpBean emp = null;
        String SQL = "SELECT * From employee WHERE employee_id= ?";
		
		
		try {
			
			 conn = getConnection();
			 pstmt = conn.prepareStatement(SQL);

			 pstmt.setString(1, empno);
			 rs = pstmt.executeQuery();

			if (rs.next()) {
				emp = new EmpBean();
				emp.setEmployeeId(rs.getInt("employee_id"));
				emp.setEname(rs.getString("ename"));
				emp.setPhone(rs.getString("phone"));
				emp.setEmail(rs.getString("email"));
				Date sqlDate = rs.getDate("hiredate");
				emp.setHiredate(sqlDate);
				emp.setProfilePhoto(rs.getString("profile_photo"));
				emp.setIsActive(rs.getBoolean("is_active"));

			}
			
			return emp; 
		} catch (SQLException | NamingException e) {
            e.printStackTrace();
            throw e; // 將例外拋出給 Controller 處理
        } finally {
            // 4. 關閉資源
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
	
	public int UpdateByEmpyId(EmpBean emp) throws SQLException, NamingException, ParseException{
		Connection conn = null;
	    PreparedStatement pstmt = null;
	    int rowsAffected = 0;
	    
	    String SQL = "UPDATE employee SET ename=?, hiredate=?, phone=?, email=?, profile_photo=?, is_active=? WHERE employee_id=?";
		
	try{
		conn = getConnection();
		
		
		
		Date existingSqlDate = emp.getHiredate();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 假設輸入格式
		String hireDateStr = sdf.format(existingSqlDate);
		java.util.Date utilDate = sdf.parse(hireDateStr);
		Date sqlDate = new java.sql.Date(utilDate.getTime());

		
	    pstmt = conn.prepareStatement(SQL);
		
		
	    pstmt.setString(1, emp.getEname());
        pstmt.setDate(2, sqlDate);
        pstmt.setString(3, emp.getPhone());
        pstmt.setString(4, emp.getEmail());
        pstmt.setString(5, emp.getProfilePhoto());
        pstmt.setBoolean(6, emp.getIsActive());
        pstmt.setInt(7, emp.getEmployeeId());
        rowsAffected = pstmt.executeUpdate();
        
        return rowsAffected;
        
	} catch (SQLException | NamingException | ParseException e) {
        e.printStackTrace();
        throw e; // 將例外拋出給 Controller 處理
    } finally {
        // 確保關閉資源
        if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
	}
	
	public List<EmpBean> getFuzzySearch(String ename) throws NamingException, SQLException {
		
		
		String SQL = "SELECT * FROM employee WHERE ename LIKE ?";
		List<EmpBean> emps = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try(Connection conn = getConnection()){
				
		  
			pstmt = conn.prepareStatement(SQL); 
			pstmt.setString(1, "%" + ename + "%");
			rs = pstmt.executeQuery();	
			EmpBean emp =null;
				while (rs.next()) {
					
					emp = new EmpBean();
					emp.setEmployeeId(rs.getInt("employee_id"));
					emp.setEname(rs.getString("ename"));
					emp.setPhone(rs.getString("phone"));
					emp.setEmail(rs.getString("email"));
					Date sqlDate = rs.getDate("hiredate");
					emp.setHiredate(sqlDate);
					emp.setProfilePhoto(rs.getString("profile_photo"));
					emp.setIsActive(rs.getBoolean("is_active"));

					emps.add(emp);
			
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
		return emps;

 }
	
	
	
	
	

	


}
