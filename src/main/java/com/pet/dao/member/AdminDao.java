package com.pet.dao.member;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.mindrot.jbcrypt.BCrypt;
import com.pet.model.member.Admin;

public class AdminDao {
	//JNDI連線
	private Connection getConnection() throws SQLException, NamingException {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
        return dataSource.getConnection();
    }
	//登入
	public Admin login(String email,String inputPassword) {
		String sql = "SELECT * FROM admin WHERE email=?";
	    Admin admin = null;
	    
	    try (Connection connection = getConnection();
	             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setString(1, email);
			
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if(resultSet.next()) {
					String hashedPassword = resultSet.getString("password");
					//比對雜湊值
					if (BCrypt.checkpw(inputPassword, hashedPassword)) {
				        admin = new Admin();
				        admin.setAdminId(resultSet.getInt("admin_id"));
				        admin.setEmail(resultSet.getString("email"));
				        admin.setPassword(hashedPassword);
				        admin.setName(resultSet.getString("name"));
				        admin.setPhone(resultSet.getString("phone"));
				        admin.setRole(resultSet.getString("role"));
				        admin.setStatus(resultSet.getString("status"));
				        admin.setCreatedAt(resultSet.getTimestamp("created_at"));
				        admin.setUpdatedAt(resultSet.getTimestamp("updated_at"));
				    }
				}
			}
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return admin;
	}
	
	//查詢全部
	public List<Admin> queryAllAdmin(){
		String sql = "SELECT * FROM admin";
		List<Admin> admins = new ArrayList<>();
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			
			while(resultSet.next()) {
				Admin admin = new Admin();
				admin.setAdminId(resultSet.getInt("admin_id"));
				admin.setEmail(resultSet.getString("email"));
				admin.setName(resultSet.getString("name"));
				admin.setPhone(resultSet.getString("phone"));
				admin.setRole(resultSet.getString("role"));
				admin.setStatus(resultSet.getString("status"));
				admins.add(admin);
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return admins;
	}
	
	//查詢啟用中管理員
	public List<Admin> queryActiveAdmins(){
		
		String sql = "SELECT * FROM admin WHERE status='active'";
		List<Admin> admins = new ArrayList<>();
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			
			while(resultSet.next()) {
				Admin admin = new Admin();
				admin.setAdminId(resultSet.getInt("admin_id"));
				admin.setEmail(resultSet.getString("email"));
				admin.setName(resultSet.getString("name"));
				admin.setPhone(resultSet.getString("phone"));
				admin.setRole(resultSet.getString("role"));
				admin.setStatus(resultSet.getString("status"));
				admins.add(admin);
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return admins;
	}

	//查詢停用管理員
	public List<Admin> queryDisabledAdmins(){
		
		String sql = "SELECT * FROM admin WHERE status='disabled'";
		List<Admin> admins = new ArrayList<>();
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			
			while(resultSet.next()) {
				Admin admin = new Admin();
				admin.setAdminId(resultSet.getInt("admin_id"));
				admin.setEmail(resultSet.getString("email"));
				admin.setName(resultSet.getString("name"));
				admin.setPhone(resultSet.getString("phone"));
				admin.setRole(resultSet.getString("role"));
				admin.setStatus(resultSet.getString("status"));
				admins.add(admin);
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return admins;
	}
			
	//依ID查詢
	public Admin queryAdminById(int id) {
		String sql = "SELECT * FROM admin WHERE admin_id=?";
		Admin admin = null;
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			
			preparedStatement.setInt(1, id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if(resultSet.next()) {
					admin = new Admin();
					admin.setAdminId(resultSet.getInt("admin_id"));
					admin.setEmail(resultSet.getString("email"));
					admin.setName(resultSet.getString("name"));
					admin.setPhone(resultSet.getString("phone"));
					admin.setRole(resultSet.getString("role"));
					admin.setStatus(resultSet.getString("status"));
				}
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return admin;
	}
	
	//依姓名模糊查詢
	public List<Admin> queryAdminsByName(String name){
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM admin WHERE name LIKE ?";
        try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, "%" + name + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Admin admin = new Admin();
                    admin.setAdminId(resultSet.getInt("admin_id"));
    				admin.setEmail(resultSet.getString("email"));
    				admin.setName(resultSet.getString("name"));
    				admin.setPhone(resultSet.getString("phone"));
    				admin.setRole(resultSet.getString("role"));
    				admin.setStatus(resultSet.getString("status"));
                    admins.add(admin);
                }
            }
        } catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return admins;
    }
	
	// 切換停用/啟用
	public boolean toggleStatusAdmin(int id) {
	    // 先查目前狀態
	    String checkSql = "SELECT status FROM admin WHERE admin_id=?";
	    String updateSql = "UPDATE admin SET status=? WHERE admin_id=?";
	    
	    try (Connection connection = getConnection();
	         PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
	        
	        checkStmt.setInt(1, id);
	        try (ResultSet resultSet = checkStmt.executeQuery()) {
	            if (resultSet.next()) {
	                String currentStatus = resultSet.getString("status");
	                String newStatus = "active".equals(currentStatus) ? "disabled" : "active";

	                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
	                    updateStmt.setString(1, newStatus);
	                    updateStmt.setInt(2, id);
	                    int affectedRows = updateStmt.executeUpdate();
	                    return affectedRows > 0;
	                }
	            } else {
	                return false; // 找不到該admin
	            }
	        }
	    } catch (SQLException | NamingException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	//新增
	public Admin createAdmin(Admin admin) {
		// 將密碼轉成 BCrypt 雜湊
	    String hashedPassword = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt());
	    admin.setPassword(hashedPassword); // 設回 admin 物件
	    
	    String sql = "INSERT INTO admin (name, email, password, phone, role) "
	               + "VALUES (?, ?, ?, ?, ?)";

	    try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

	        preparedStatement.setString(1, admin.getName());
	        preparedStatement.setString(2, admin.getEmail());
	        preparedStatement.setString(3, admin.getPassword());
	        preparedStatement.setString(4, admin.getPhone());
	        preparedStatement.setString(5, admin.getRole());
	       

	        int result = preparedStatement.executeUpdate();
	        
	        
	        if (result > 0) {
	            // 取得自動生成的 ID
	            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    int id = generatedKeys.getInt(1);
	                    admin.setAdminId(id); // 設回 Admin 物件
	                }
	            }
	        } 
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    } catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		return admin;
	}
	//修改
	public Admin updateAdmin(Admin admin) {
        String sql = "UPDATE admin SET name=?, email=?, phone=?, role=? WHERE admin_id=?";
        try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, admin.getName());
            preparedStatement.setString(2, admin.getEmail());
            preparedStatement.setString(3, admin.getPhone());
            preparedStatement.setString(4, admin.getRole());
            preparedStatement.setInt(5, admin.getAdminId());
            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                return queryAdminById(admin.getAdminId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        return null;
    }
	
    
}
