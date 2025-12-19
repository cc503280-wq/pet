package com.pet.dao.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import com.pet.model.member.MemberPet;

public class MemberPetDao {
	//連線
	private Connection getConnection() throws SQLException, NamingException {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
        return dataSource.getConnection();
    }
	
	//查詢全部
	public List<MemberPet> queryAllMemberPets(){
		
		String sql = "SELECT * FROM member_pets ORDER BY pet_id";
		List<MemberPet> memberPets = new ArrayList<>();
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			
			while(resultSet.next()) {
				MemberPet memberPet = new MemberPet();
				memberPet.setPetId(resultSet.getInt("pet_id"));
				memberPet.setMemberId(resultSet.getInt("member_id"));
				memberPet.setPetName(resultSet.getString("pet_name"));
				memberPet.setPetType(resultSet.getString("pet_type"));
				memberPet.setPetBreed(resultSet.getString("pet_breed"));
				memberPet.setPetAge(resultSet.getString("pet_age"));
				memberPet.setPetSize(resultSet.getString("pet_size"));
				
				memberPets.add(memberPet);
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return memberPets;
	}
	
	//依petId查詢
	public MemberPet queryMemberPetByPetId(int id) {
		String sql = "SELECT * FROM member_pets WHERE pet_id=?";
		MemberPet memberPet = null;
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			
			preparedStatement.setInt(1, id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if(resultSet.next()) {
					memberPet = new MemberPet();
					memberPet.setPetId(resultSet.getInt("pet_id"));
					memberPet.setMemberId(resultSet.getInt("member_id"));
					memberPet.setPetName(resultSet.getString("pet_name"));
					memberPet.setPetType(resultSet.getString("pet_type"));
					memberPet.setPetBreed(resultSet.getString("pet_breed"));
					memberPet.setPetAge(resultSet.getString("pet_age"));
					memberPet.setPetSize(resultSet.getString("pet_size"));
				}
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return memberPet;
	}
	//依memberId查詢
	public List<MemberPet> queryMemberPetByMemberId(int id) {
		String sql = "SELECT * FROM member_pets WHERE member_id=? ORDER BY pet_id";
		MemberPet memberPet = null;
		List<MemberPet> memberPets = new ArrayList<>();
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			
			preparedStatement.setInt(1, id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					memberPet = new MemberPet();
					memberPet.setPetId(resultSet.getInt("pet_id"));
					memberPet.setMemberId(resultSet.getInt("member_id"));
					memberPet.setPetName(resultSet.getString("pet_name"));
					memberPet.setPetType(resultSet.getString("pet_type"));
					memberPet.setPetBreed(resultSet.getString("pet_breed"));
					memberPet.setPetAge(resultSet.getString("pet_age"));
					memberPet.setPetSize(resultSet.getString("pet_size"));
					memberPets.add(memberPet);
				}
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return memberPets;
	}
	
	public List<MemberPet> queryPetsByConditions(String type, String age, String size){
		List<MemberPet> memberPets = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder("SELECT * FROM member_pets WHERE 1=1");
		
		if (type != null && !type.isEmpty()) {
	        sql.append(" AND pet_type = ?");
	    }
	    if (age != null && !age.isEmpty()) {
	        sql.append(" AND pet_age = ?");
	    }
	    if (size != null && !size.isEmpty()) {
	        sql.append(" AND pet_size = ?");
	    }
	    
	    try (Connection connection = getConnection();
	            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

	           int paramIndex = 1;

	           if (type != null && !type.isEmpty()) {
	               preparedStatement.setString(paramIndex++, type);
	           }
	           if (age != null && !age.isEmpty()) {
	               preparedStatement.setString(paramIndex++, age);
	           }
	           if (size != null && !size.isEmpty()) {
	               preparedStatement.setString(paramIndex++, size);
	           }

	           ResultSet resultSet = preparedStatement.executeQuery();
	           while (resultSet.next()) {
	        	   MemberPet memberPet = new MemberPet();
				   memberPet.setPetId(resultSet.getInt("pet_id"));
				   memberPet.setMemberId(resultSet.getInt("member_id"));
				   memberPet.setPetName(resultSet.getString("pet_name"));
				   memberPet.setPetType(resultSet.getString("pet_type"));
				   memberPet.setPetBreed(resultSet.getString("pet_breed"));
				   memberPet.setPetAge(resultSet.getString("pet_age"));
				   memberPet.setPetSize(resultSet.getString("pet_size"));
				
				   memberPets.add(memberPet);;
	           }

	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	       return memberPets;
	   }
	
public List<MemberPetsBean> findByMemberId(int memberId) throws Exception{
		
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;        
        List<MemberPetsBean> mpbs = new ArrayList<>();
        String SQL = "SELECT pet_id, pet_name, pet_type From member_pets WHERE member_id= ?";
		
		
		try {
			
			 conn = getConnection();
			 pstmt = conn.prepareStatement(SQL);

			 pstmt.setInt(1, memberId);
			 rs = pstmt.executeQuery();
			 MemberPetsBean mpb =null;
			 while (rs.next()) {
			
				mpb = new MemberPetsBean();
				mpb.setPetId(rs.getInt("pet_id"));
				mpb.setPetName(rs.getString("pet_name"));
				mpb.setPetType(rs.getString("pet_type"));
				mpbs.add(mpb);
			}
			
			return mpbs; 
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
	
	
	
}
