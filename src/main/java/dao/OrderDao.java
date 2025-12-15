package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.orderBean;
import utils.JDBCUtil;

public class OrderDao {
	
	/**
	 * 插入訂單並回傳剛產生的 order_id
	 * 
	 * @return order_id 若失敗回傳 -1
	 */
	public int insertOrder(int memberId, Date orderDate, String status, BigDecimal totalAmountUndiscount,
			Integer couponId, // 可為 null
			BigDecimal totalAmountDiscount, int usePoints, BigDecimal totalAmountDiscountPoints, int getPoints) {
		// 使用 OUTPUT INSERTED.order_id 取得剛插入的自增欄位
		String sql = "INSERT INTO Orders "
				+ "(member_id, order_date, status, total_amount_undiscount, coupon_id, total_amount_discount, use_points, total_amount_discount_points, get_points) "
				+ "OUTPUT INSERTED.order_id " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection connection = JDBCUtil.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			ps.setTimestamp(2, new Timestamp(orderDate.getTime()));
			ps.setString(3, status);
			ps.setBigDecimal(4, totalAmountUndiscount);

			if (couponId != null) {
				ps.setInt(5, couponId);
			} else {
				ps.setNull(5, java.sql.Types.INTEGER);
			}

			ps.setBigDecimal(6, totalAmountDiscount);
			ps.setInt(7, usePoints);
			ps.setBigDecimal(8, totalAmountDiscountPoints);
			ps.setInt(9, getPoints);

			// 執行並取得回傳 order_id
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1); // 回傳 order_id
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1; // 插入失敗回傳 -1
	}

	public List<orderBean> findAllOrders() {
		List<orderBean> orderList = new ArrayList<orderBean>();
		String sql = "SELECT * FROM orders";
		
		try (Connection connection = JDBCUtil.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);
						ResultSet resultSet=ps.executeQuery();) {
			
			while (resultSet.next()) {
				Integer orderId = resultSet.getInt("order_id");
				Integer memberId = resultSet.getInt("member_id");
				Date orderDate = resultSet.getDate("order_date");
				String status = resultSet.getString("status");
				Double totalAmountUndiscount = resultSet.getDouble("total_amount_undiscount");
				Integer couponId = resultSet.getInt("coupon_id");
				Double totalAmountDiscount = resultSet.getDouble("total_amount_discount");
				Integer usePoints = resultSet.getInt("use_points");
				Double totalAmountDiscountPoints = resultSet.getDouble("total_amount_discount_points");
				Integer getPoints = resultSet.getInt("get_points");
				orderBean orderBean = new orderBean(orderId,memberId,orderDate,status,totalAmountUndiscount,couponId,totalAmountDiscount,usePoints,totalAmountDiscountPoints,getPoints);
				orderList.add(orderBean);
			}
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return orderList;

	}
	public List<orderBean> findIdOrders(Integer memberId) {
		List<orderBean> orderList = new ArrayList<orderBean>();
		String sql = "SELECT * FROM orders where member_id=?";
		try (Connection connection = JDBCUtil.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try(ResultSet resultSet=ps.executeQuery();) {
				while (resultSet.next()) {
					Integer orderId = resultSet.getInt("order_id");
					memberId = resultSet.getInt("member_id");
					Date orderDate = resultSet.getDate("order_date");
					String status = resultSet.getString("status");
					Double totalAmountUndiscount = resultSet.getDouble("total_amount_undiscount");
					Integer couponId = resultSet.getInt("coupon_id");
					Double totalAmountDiscount = resultSet.getDouble("total_amount_discount");
					Integer usePoints = resultSet.getInt("use_points");
					Double totalAmountDiscountPoints = resultSet.getDouble("total_amount_discount_points");
					Integer getPoints = resultSet.getInt("get_points");
					orderBean orderBean = new orderBean(orderId,memberId,orderDate,status,totalAmountUndiscount,couponId,totalAmountDiscount,usePoints,totalAmountDiscountPoints,getPoints);
					orderList.add(orderBean);
				}
				
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orderList;

	}
	
	public void changeOrder(String status,Integer id) {
		String sql = "UPDATE orders set status = ? where order_id=?";
		try (Connection connection = JDBCUtil.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setInt(2, id);
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
