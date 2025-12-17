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

import com.pet.model.appoinment.MemberBean;
import com.pet.model.member.Member;

public class MemberDao {
	//連線
	private Connection getConnection() throws SQLException, NamingException {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
        return dataSource.getConnection();
    }
	
	//查詢全部
	public List<Member> queryAllMembers(){
		
		String sql = "SELECT * FROM members";
		List<Member> members = new ArrayList<>();
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			
			while(resultSet.next()) {
				Member member = new Member();
				member.setMemberId(resultSet.getInt("member_id"));
				member.setEmail(resultSet.getString("email"));
				member.setName(resultSet.getString("name"));
				member.setGender(resultSet.getString("gender"));
				member.setBirthday(resultSet.getDate("birthday"));
				member.setPhone(resultSet.getString("phone"));
				member.setAddress(resultSet.getString("address"));
				member.setPicture(resultSet.getString("picture"));
				member.setStatus(resultSet.getString("status"));
				member.setOauthType(resultSet.getString("oauth_type"));
				member.setOauthId(resultSet.getString("oauth_id"));
				member.setPoints(resultSet.getInt("points"));
				members.add(member);
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return members;
	}
	
	//查詢啟用中會員
		public List<Member> queryActiveMembers(){
			
			String sql = "SELECT * FROM members WHERE status='active'";
			List<Member> members = new ArrayList<>();
			
			try (Connection connection = getConnection();
					PreparedStatement preparedStatement = connection.prepareStatement(sql);
					ResultSet resultSet = preparedStatement.executeQuery()) {
				
				while(resultSet.next()) {
					Member member = new Member();
					member.setMemberId(resultSet.getInt("member_id"));
					member.setEmail(resultSet.getString("email"));
					member.setName(resultSet.getString("name"));
					member.setGender(resultSet.getString("gender"));
					member.setBirthday(resultSet.getDate("birthday"));
					member.setPhone(resultSet.getString("phone"));
					member.setAddress(resultSet.getString("address"));
					member.setPicture(resultSet.getString("picture"));
					member.setStatus(resultSet.getString("status"));
					member.setOauthType(resultSet.getString("oauth_type"));
					member.setOauthId(resultSet.getString("oauth_id"));
					member.setPoints(resultSet.getInt("points"));
					members.add(member);
				}
			} catch (SQLException | NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return members;
		}
	
		//查詢停用會員
		public List<Member> queryDisabledMembers(){
			
			String sql = "SELECT * FROM members WHERE status='disabled'";
			List<Member> members = new ArrayList<>();
			
			try (Connection connection = getConnection();
					PreparedStatement preparedStatement = connection.prepareStatement(sql);
					ResultSet resultSet = preparedStatement.executeQuery()) {
				
				while(resultSet.next()) {
					Member member = new Member();
					member.setMemberId(resultSet.getInt("member_id"));
					member.setEmail(resultSet.getString("email"));
					member.setName(resultSet.getString("name"));
					member.setGender(resultSet.getString("gender"));
					member.setBirthday(resultSet.getDate("birthday"));
					member.setPhone(resultSet.getString("phone"));
					member.setAddress(resultSet.getString("address"));
					member.setPicture(resultSet.getString("picture"));
					member.setStatus(resultSet.getString("status"));
					member.setOauthType(resultSet.getString("oauth_type"));
					member.setOauthId(resultSet.getString("oauth_id"));
					member.setPoints(resultSet.getInt("points"));
					members.add(member);
				}
			} catch (SQLException | NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return members;
		}
		
	//依ID查詢
	public Member queryMemberById(int id) {
		String sql = "SELECT * FROM members WHERE member_id=?";
		Member member = null;
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			
			preparedStatement.setInt(1, id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if(resultSet.next()) {
					member = new Member();
					member.setMemberId(resultSet.getInt("member_id"));
					member.setEmail(resultSet.getString("email"));
					member.setName(resultSet.getString("name"));
					member.setGender(resultSet.getString("gender"));
					member.setBirthday(resultSet.getDate("birthday"));
					member.setPhone(resultSet.getString("phone"));
					member.setAddress(resultSet.getString("address"));
					member.setPicture(resultSet.getString("picture"));
					member.setStatus(resultSet.getString("status"));
					member.setOauthType(resultSet.getString("oauth_type"));
					member.setOauthId(resultSet.getString("oauth_id"));
					member.setPoints(resultSet.getInt("points"));
				}
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return member;
	}
	
	//依姓名模糊查詢
	public List<Member> queryMembersByName(String name){
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE name LIKE ?";
        try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, "%" + name + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                	Member member = new Member();
					member.setMemberId(resultSet.getInt("member_id"));
					member.setEmail(resultSet.getString("email"));
					member.setName(resultSet.getString("name"));
					member.setGender(resultSet.getString("gender"));
					member.setBirthday(resultSet.getDate("birthday"));
					member.setPhone(resultSet.getString("phone"));
					member.setAddress(resultSet.getString("address"));
					member.setPicture(resultSet.getString("picture"));
					member.setStatus(resultSet.getString("status"));
					member.setOauthType(resultSet.getString("oauth_type"));
					member.setOauthId(resultSet.getString("oauth_id"));
					member.setPoints(resultSet.getInt("points"));
                    members.add(member);
                }
            }
        } catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return members;
    }
	
	// 切換停用/啟用(軟刪除)
	public boolean toggleStatusMember(int id) {
	    // 先查目前狀態再修改狀態
	    String checkSql = "SELECT status FROM members WHERE member_id=?";
	    String updateSql = "UPDATE members SET status=? WHERE member_id=?";
	    
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
	                return false; // 找不到該member
	            }
	        }
	    } catch (SQLException | NamingException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	 
	//新增
	public Member createMember(Member member) {
		// 將密碼轉成 BCrypt 雜湊
	    String hashedPassword = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());
	    member.setPassword(hashedPassword); // 設回 member 物件
	    
	    String sql = "INSERT INTO members (email, password, name, gender, birthday, phone, address, picture) "
	               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	    try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

	        preparedStatement.setString(1, member.getEmail());
	        preparedStatement.setString(2, member.getPassword());
	        preparedStatement.setString(3, member.getName());
	        preparedStatement.setString(4, member.getGender());
	        preparedStatement.setDate(5, new java.sql.Date(member.getBirthday().getTime()));
	        preparedStatement.setString(6, member.getPhone());
	        preparedStatement.setString(7, member.getAddress());
	        preparedStatement.setString(8, member.getPicture());

	        int result = preparedStatement.executeUpdate();
	        
	        
	        if (result > 0) {
	            // 取得自動生成的 ID
	            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    int id = generatedKeys.getInt(1);
	                    member.setMemberId(id); // 設回 member 物件
	                    return queryMemberById(member.getMemberId());
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
		return null;
	}
	//修改
	public Member updateMember(Member member) {
        String sql = "UPDATE members SET email=?, name=?, gender=?, birthday=?, phone=?, address=?, picture=? WHERE member_id=?";
        try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, member.getEmail());
            preparedStatement.setString(2, member.getName());
            preparedStatement.setString(3, member.getGender());
            preparedStatement.setDate(4, new java.sql.Date(member.getBirthday().getTime()));
            preparedStatement.setString(5, member.getPhone());
            preparedStatement.setString(6, member.getAddress());
            preparedStatement.setString(7, member.getPicture());
            preparedStatement.setInt(8, member.getMemberId());
            int rows = preparedStatement.executeUpdate();
            System.out.println(rows);
            if (rows > 0) {
                return queryMemberById(member.getMemberId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
    }
	
	public String getMemberNameById(Integer memberId) throws SQLException, NamingException {

		final String SQL = "SELECT name FROM members WHERE member_id = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String memberName = null;

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, memberId);
			rs = pstmt.executeQuery();
			if (rs.next()) {            
                memberName = rs.getString("name");
			}
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
		return memberName;
	}
	
	public List<MemberBean> SearchAll() throws SQLException, NamingException {

		final String SQL = "SELECT member_id, name FROM members";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<MemberBean> members = new ArrayList<>();

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			MemberBean member = null;
			while (rs.next()) {
				
				member=new MemberBean();
				member.setMemberId(rs.getInt("member_id"));
				member.setName(rs.getString("name"));	
				members.add(member);
			}
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
		return members;
	}
	
	
}
