package com.pet.dao.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.pet.model.member.Coupon;

public class CouponDao {
	//連線
	private Connection getConnection() throws SQLException, NamingException {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
        return dataSource.getConnection();
    }
	
	//查詢全部
	public List<Coupon> queryAllCoupons(){
		
		String sql = "SELECT * FROM coupons";
		List<Coupon> coupons = new ArrayList<>();
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			
			while(resultSet.next()) { 
				Coupon coupon = new Coupon();
				coupon.setCouponId(resultSet.getInt("coupon_id"));
				coupon.setCode(resultSet.getString("code"));
				coupon.setDiscountType(resultSet.getString("discount_type"));
				coupon.setDiscountValue(resultSet.getDouble("discount_value"));
				coupon.setIsLimited(resultSet.getInt("is_limited"));
				coupon.setTotalAmount(resultSet.getInt("total_amount"));
				coupon.setIssuedAmount(resultSet.getInt("issued_amount"));
				coupon.setIssueStartAt(resultSet.getDate("issue_start_at"));
				coupon.setIssueEndAt(resultSet.getDate("issue_end_at"));
				coupon.setUseStartAt(resultSet.getDate("use_start_at"));
				coupon.setUseEndAt(resultSet.getDate("use_end_at"));
				coupon.setMinPurchase(resultSet.getInt("min_purchase"));
				coupon.setCreatedAt(resultSet.getTimestamp("created_at"));
				coupon.setUpdatedAt(resultSet.getTimestamp("updated_at"));
				coupon.setStatus(resultSet.getString("status"));
                coupons.add(coupon);
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return coupons;
	}
	
	//查詢啟用中
		public List<Coupon> queryActiveCoupons(){
			
			String sql = "SELECT * FROM coupons WHERE status='active'";
			List<Coupon> coupons = new ArrayList<>();
			
			try (Connection connection = getConnection();
					PreparedStatement preparedStatement = connection.prepareStatement(sql);
					ResultSet resultSet = preparedStatement.executeQuery()) {
				
				while(resultSet.next()) {
					Coupon coupon = new Coupon();
					coupon.setCouponId(resultSet.getInt("coupon_id"));
					coupon.setCode(resultSet.getString("code"));
					coupon.setDiscountType(resultSet.getString("discount_type"));
					coupon.setDiscountValue(resultSet.getDouble("discount_value"));
					coupon.setIsLimited(resultSet.getInt("is_limited"));
					coupon.setTotalAmount(resultSet.getInt("total_amount"));
					coupon.setIssuedAmount(resultSet.getInt("issued_amount"));
					coupon.setIssueStartAt(resultSet.getDate("issue_start_at"));
					coupon.setIssueEndAt(resultSet.getDate("issue_end_at"));
					coupon.setUseStartAt(resultSet.getDate("use_start_at"));
					coupon.setUseEndAt(resultSet.getDate("use_end_at"));
					coupon.setMinPurchase(resultSet.getInt("min_purchase"));
					coupon.setCreatedAt(resultSet.getTimestamp("created_at"));
					coupon.setUpdatedAt(resultSet.getTimestamp("updated_at"));
					coupon.setStatus(resultSet.getString("status"));
                    coupons.add(coupon);
				}
			} catch (SQLException | NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return coupons;
		}
	
		//查詢停用
		public List<Coupon> queryDisabledCoupons(){
			
			String sql = "SELECT * FROM coupons WHERE status='disabled'";
			List<Coupon> coupons = new ArrayList<>(); 
			
			try (Connection connection = getConnection();
					PreparedStatement preparedStatement = connection.prepareStatement(sql);
					ResultSet resultSet = preparedStatement.executeQuery()) {
				
				while(resultSet.next()) {
					Coupon coupon = new Coupon();
					coupon.setCouponId(resultSet.getInt("coupon_id"));
					coupon.setCode(resultSet.getString("code"));
					coupon.setDiscountType(resultSet.getString("discount_type"));
					coupon.setDiscountValue(resultSet.getDouble("discount_value"));
					coupon.setIsLimited(resultSet.getInt("is_limited"));
					coupon.setTotalAmount(resultSet.getInt("total_amount"));
					coupon.setIssuedAmount(resultSet.getInt("issued_amount"));
					coupon.setIssueStartAt(resultSet.getDate("issue_start_at"));
					coupon.setIssueEndAt(resultSet.getDate("issue_end_at"));
					coupon.setUseStartAt(resultSet.getDate("use_start_at"));
					coupon.setUseEndAt(resultSet.getDate("use_end_at"));
					coupon.setMinPurchase(resultSet.getInt("min_purchase"));
					coupon.setCreatedAt(resultSet.getTimestamp("created_at"));
					coupon.setUpdatedAt(resultSet.getTimestamp("updated_at"));
					coupon.setStatus(resultSet.getString("status"));
                    coupons.add(coupon);
				}
			} catch (SQLException | NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return coupons;
		}
		
	//依ID查詢
	public Coupon queryCouponById(int id) {
		String sql = "SELECT * FROM coupons WHERE coupon_id=?";
		Coupon coupon = null; 
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			
			preparedStatement.setInt(1, id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if(resultSet.next()) {
					coupon = new Coupon();
					coupon.setCouponId(resultSet.getInt("coupon_id"));
					coupon.setCode(resultSet.getString("code"));
					coupon.setDiscountType(resultSet.getString("discount_type"));
					coupon.setDiscountValue(resultSet.getDouble("discount_value"));
					coupon.setIsLimited(resultSet.getInt("is_limited"));
					coupon.setTotalAmount(resultSet.getInt("total_amount"));
					coupon.setIssuedAmount(resultSet.getInt("issued_amount"));
					coupon.setIssueStartAt(resultSet.getDate("issue_start_at"));
					coupon.setIssueEndAt(resultSet.getDate("issue_end_at"));
					coupon.setUseStartAt(resultSet.getDate("use_start_at"));
					coupon.setUseEndAt(resultSet.getDate("use_end_at"));
					coupon.setMinPurchase(resultSet.getInt("min_purchase"));
					coupon.setCreatedAt(resultSet.getTimestamp("created_at"));
					coupon.setUpdatedAt(resultSet.getTimestamp("updated_at"));
					coupon.setStatus(resultSet.getString("status"));
				}
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return coupon;
	}
	
	//依發放期間查詢
	public List<Coupon> queryCouponsByIssueRange(Date start, Date end){
		List<Coupon> coupons = new ArrayList<>();
		String sql = "SELECT * FROM coupons WHERE issue_start_at <= ? AND issue_end_at >= ?";
		
		try (Connection connection = getConnection();
		         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

		        // 設定參數
		        preparedStatement.setDate(2, new java.sql.Date(start.getTime()));
		        preparedStatement.setDate(1, new java.sql.Date(end.getTime()));

		        try (ResultSet resultSet = preparedStatement.executeQuery()) {
		            while (resultSet.next()) {
		            	Coupon coupon = new Coupon();
						coupon.setCouponId(resultSet.getInt("coupon_id"));
						coupon.setCode(resultSet.getString("code"));
						coupon.setDiscountType(resultSet.getString("discount_type"));
						coupon.setDiscountValue(resultSet.getDouble("discount_value"));
						coupon.setIsLimited(resultSet.getInt("is_limited"));
						coupon.setTotalAmount(resultSet.getInt("total_amount"));
						coupon.setIssuedAmount(resultSet.getInt("issued_amount"));
						coupon.setIssueStartAt(resultSet.getDate("issue_start_at"));
						coupon.setIssueEndAt(resultSet.getDate("issue_end_at"));
						coupon.setUseStartAt(resultSet.getDate("use_start_at"));
						coupon.setUseEndAt(resultSet.getDate("use_end_at"));
						coupon.setMinPurchase(resultSet.getInt("min_purchase"));
						coupon.setCreatedAt(resultSet.getTimestamp("created_at"));
						coupon.setUpdatedAt(resultSet.getTimestamp("updated_at"));
						coupon.setStatus(resultSet.getString("status"));
	                    coupons.add(coupon);
		            }
		        }

		    } catch (SQLException | NamingException e) {
		        e.printStackTrace();
		    }

		    return coupons;
		}
	

	//依使用期間查詢
	public List<Coupon> queryCouponsByUseRange(Date start, Date end){
		List<Coupon> coupons = new ArrayList<>();
		String sql = "SELECT * FROM coupons WHERE use_start_at <= ? AND use_end_at >= ?";
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			// 設定參數
			preparedStatement.setDate(2, new java.sql.Date(start.getTime()));
			preparedStatement.setDate(1, new java.sql.Date(end.getTime()));
			
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Coupon coupon = new Coupon();
					coupon.setCouponId(resultSet.getInt("coupon_id"));
					coupon.setCode(resultSet.getString("code"));
					coupon.setDiscountType(resultSet.getString("discount_type"));
					coupon.setDiscountValue(resultSet.getDouble("discount_value"));
					coupon.setIsLimited(resultSet.getInt("is_limited"));
					coupon.setTotalAmount(resultSet.getInt("total_amount"));
					coupon.setIssuedAmount(resultSet.getInt("issued_amount"));
					coupon.setIssueStartAt(resultSet.getDate("issue_start_at"));
					coupon.setIssueEndAt(resultSet.getDate("issue_end_at"));
					coupon.setUseStartAt(resultSet.getDate("use_start_at"));
					coupon.setUseEndAt(resultSet.getDate("use_end_at"));
					coupon.setMinPurchase(resultSet.getInt("min_purchase"));
					coupon.setCreatedAt(resultSet.getTimestamp("created_at"));
					coupon.setUpdatedAt(resultSet.getTimestamp("updated_at"));
					coupon.setStatus(resultSet.getString("status"));
					coupons.add(coupon);
				}
			}
			
		} catch (SQLException | NamingException e) {
			e.printStackTrace();
		}
		
		return coupons;
	}

	
	// 切換停用/啟用
	public boolean toggleStatusCoupon(int id) {
	    // 先查目前狀態
	    String checkSql = "SELECT status FROM coupons WHERE coupon_id=?";
	    String updateSql = "UPDATE coupons SET status=? WHERE coupon_id=?";
	    
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
	                return false; 
	            }
	        }
	    } catch (SQLException | NamingException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	 
	//新增
	public Coupon createCoupon(Coupon coupon) {
	    
	    String sql = "INSERT INTO coupons (code, discount_type, discount_value, is_limited, total_amount, issued_amount, "
				+ "issue_start_at, issue_end_at, use_start_at, use_end_at, min_purchase) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

	    	preparedStatement.setString(1, coupon.getCode());
			preparedStatement.setString(2, coupon.getDiscountType());
			preparedStatement.setDouble(3, coupon.getDiscountValue());
			preparedStatement.setInt(4, coupon.getIsLimited());
			preparedStatement.setObject(5, coupon.getTotalAmount(), java.sql.Types.INTEGER); //可以接受null
			preparedStatement.setObject(6, coupon.getIssuedAmount(), java.sql.Types.INTEGER);
			preparedStatement.setDate(7, new java.sql.Date(coupon.getIssueStartAt().getTime()));
			preparedStatement.setDate(8, new java.sql.Date(coupon.getIssueEndAt().getTime()));
			preparedStatement.setDate(9, new java.sql.Date(coupon.getUseStartAt().getTime()));
			preparedStatement.setDate(10, new java.sql.Date(coupon.getUseEndAt().getTime()));
			preparedStatement.setInt(11, coupon.getMinPurchase());

	        int result = preparedStatement.executeUpdate();
	        
	        
	        if (result > 0) {
	            // 取得自動生成的 ID
	            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    int id = generatedKeys.getInt(1);
	                    coupon.setCouponId(id); 
	                    return queryCouponById(coupon.getCouponId());
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
	public Coupon updateCoupon(Coupon coupon) {
		String sql = "UPDATE coupons SET code=?, discount_type=?, discount_value=?, is_limited=?, "
				+ "total_amount=?, issued_amount=?, issue_start_at=?, issue_end_at=?, "
				+ "use_start_at=?, use_end_at=?, min_purchase=? WHERE coupon_id=? ";
				
        try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        	preparedStatement.setString(1, coupon.getCode());
			preparedStatement.setString(2, coupon.getDiscountType());
			preparedStatement.setDouble(3, coupon.getDiscountValue());
			preparedStatement.setInt(4, coupon.getIsLimited());
			preparedStatement.setObject(5, coupon.getTotalAmount(), java.sql.Types.INTEGER);
			preparedStatement.setObject(6, coupon.getIssuedAmount(), java.sql.Types.INTEGER);
			preparedStatement.setDate(7, new java.sql.Date(coupon.getIssueStartAt().getTime()));
			preparedStatement.setDate(8, new java.sql.Date(coupon.getIssueEndAt().getTime()));
			preparedStatement.setDate(9, new java.sql.Date(coupon.getUseStartAt().getTime()));
			preparedStatement.setDate(10, new java.sql.Date(coupon.getUseEndAt().getTime()));
			preparedStatement.setInt(11, coupon.getMinPurchase());
			preparedStatement.setInt(12, coupon.getCouponId());
            int rows = preparedStatement.executeUpdate();
            System.out.println(rows);
            if (rows > 0) {
                return queryCouponById(coupon.getCouponId());
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
