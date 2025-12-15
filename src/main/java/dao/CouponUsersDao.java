package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.CouponUsers;

public class CouponUsersDao {
	// 連線
	private Connection getConnection() throws SQLException, NamingException {
		InitialContext context = new InitialContext();
		DataSource dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
		return dataSource.getConnection();
	}

	// 查詢全部
	public List<CouponUsers> queryAllCouponUsers() {

		String sql = "SELECT * FROM member_coupon_view ORDER BY coupon_id";
		List<CouponUsers> couponUsers = new ArrayList<>();

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				CouponUsers couponUser = new CouponUsers();
				couponUser.setId(resultSet.getInt("id"));
				couponUser.setCouponId(resultSet.getInt("coupon_id"));
				couponUser.setMemberId(resultSet.getInt("member_id"));
				couponUser.setStatus(resultSet.getString("status"));
				couponUser.setAssignedAt(resultSet.getTimestamp("assigned_at"));
				couponUser.setUsedAt(
						resultSet.getTimestamp("used_at") != null ? resultSet.getTimestamp("used_at") : null); //可以不需要三元運算也會傳null
				couponUser.setCode(resultSet.getString("code"));
				couponUser.setDiscountType(resultSet.getString("discount_type"));
				couponUser.setDiscountValue(resultSet.getDouble("discount_value"));
				couponUser.setMinPurchase(resultSet.getInt("min_purchase"));
				couponUser.setIssueStartAt(resultSet.getDate("issue_start_at"));
				couponUser.setIssueEndAt(resultSet.getDate("issue_end_at"));
				couponUser.setUseStartAt(resultSet.getDate("use_start_at"));
				couponUser.setUseEndAt(resultSet.getDate("use_end_at"));
				couponUser.setIsExpired(resultSet.getString("is_expired"));

				couponUsers.add(couponUser);
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return couponUsers;
	}

	// 依couponId查詢
	public List<CouponUsers> queryCouponUsersByCouponId(int id) {
		String sql = "SELECT * FROM member_coupon_view WHERE coupon_id=?";
		List<CouponUsers> couponUsers = new ArrayList<>();

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					CouponUsers couponUser = new CouponUsers();
					couponUser.setId(resultSet.getInt("id"));
					couponUser.setCouponId(resultSet.getInt("coupon_id"));
					couponUser.setMemberId(resultSet.getInt("member_id"));
					couponUser.setStatus(resultSet.getString("status"));
					couponUser.setAssignedAt(resultSet.getTimestamp("assigned_at"));
					couponUser.setUsedAt(
							resultSet.getTimestamp("used_at") != null ? resultSet.getTimestamp("used_at") : null);
					couponUser.setCode(resultSet.getString("code"));
					couponUser.setDiscountType(resultSet.getString("discount_type"));
					couponUser.setDiscountValue(resultSet.getDouble("discount_value"));
					couponUser.setMinPurchase(resultSet.getInt("min_purchase"));
					couponUser.setIssueStartAt(resultSet.getDate("issue_start_at"));
					couponUser.setIssueEndAt(resultSet.getDate("issue_end_at"));
					couponUser.setUseStartAt(resultSet.getDate("use_start_at"));
					couponUser.setUseEndAt(resultSet.getDate("use_end_at"));
					couponUser.setIsExpired(resultSet.getString("is_expired"));

					couponUsers.add(couponUser);
				}
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return couponUsers;
	}

	// 依memberId查詢
	public List<CouponUsers> queryCouponUsersByMemberId(int id) {
		String sql = "SELECT * FROM member_coupon_view WHERE member_id=?";
		List<CouponUsers> couponUsers = new ArrayList<>();

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					CouponUsers couponUser = new CouponUsers();
					couponUser.setId(resultSet.getInt("id"));
					couponUser.setCouponId(resultSet.getInt("coupon_id"));
					couponUser.setMemberId(resultSet.getInt("member_id"));
					couponUser.setStatus(resultSet.getString("status"));
					couponUser.setAssignedAt(resultSet.getTimestamp("assigned_at"));
					couponUser.setUsedAt(
							resultSet.getTimestamp("used_at") != null ? resultSet.getTimestamp("used_at") : null);
					couponUser.setCode(resultSet.getString("code"));
					couponUser.setDiscountType(resultSet.getString("discount_type"));
					couponUser.setDiscountValue(resultSet.getDouble("discount_value"));
					couponUser.setMinPurchase(resultSet.getInt("min_purchase"));
					couponUser.setIssueStartAt(resultSet.getDate("issue_start_at"));
					couponUser.setIssueEndAt(resultSet.getDate("issue_end_at"));
					couponUser.setUseStartAt(resultSet.getDate("use_start_at"));
					couponUser.setUseEndAt(resultSet.getDate("use_end_at"));
					couponUser.setIsExpired(resultSet.getString("is_expired"));

					couponUsers.add(couponUser);
				}
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return couponUsers;
	}

	public void usedcoupon(Integer couponId) {
		String sql = "update coupon_users set status = ? ,used_at=? where coupon_id=?";
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, "used");
			preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			preparedStatement.setInt(3, couponId);
			preparedStatement.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
