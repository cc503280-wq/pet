package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.shipmentsBean;

import utils.JDBCUtil;

public class ShipmentsDao {

	public boolean insertShipment(Integer orderId, String shippingMethod, Integer shippingFee, String recipientName,
			String recipientPhone, String shippingAddress

	) {
		// 使用 OUTPUT INSERTED.order_id 取得剛插入的自增欄位
		String sql = "insert INTO Shipments(order_id,shipping_method,shipping_fee,recipient_name,recipient_phone,shipping_address)values (?,?,?,?,?,?)";

		try (Connection conn = JDBCUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, orderId);
			ps.setString(2, shippingMethod);
			ps.setInt(3, shippingFee);
			ps.setString(4, recipientName);
			ps.setString(5, recipientPhone);
			ps.setString(6, shippingAddress);

			int rows = ps.executeUpdate();
			return rows > 0; // 插入成功返回 true

		}

		catch (SQLException e) {
			e.printStackTrace();
			return false;

		}
	}

	public List<shipmentsBean> findAllShipments() {
		List<shipmentsBean> shipmentList = new ArrayList<shipmentsBean>();
		String sql = "SELECT * FROM Shipments";
		try (Connection conn = JDBCUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Integer shipmentId = rs.getInt("shipment_id");
				Integer orderId = rs.getInt("order_id");
				String shippingMethod = rs.getString("shipping_method");
				Integer shippingFee = rs.getInt("shipping_fee");
				String trackingNumber = rs.getString("tracking_number");
				Date shippedAt = rs.getTimestamp("shipped_at");
				Date deliveredAt = rs.getTimestamp("delivered_at");
				String status = rs.getString("status");
				String recipientName = rs.getString("recipient_name");
				String recipientPhone = rs.getString("recipient_phone");
				String shippingAddress = rs.getString("shipping_address");
				shipmentsBean shipmentsBean = new shipmentsBean(shipmentId, orderId, shippingMethod, shippingFee,
						trackingNumber, shippedAt, deliveredAt, status, recipientName, recipientPhone, shippingAddress);
				shipmentList.add(shipmentsBean);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return shipmentList;

	}

	public List<shipmentsBean> findOrderShipments(Integer orderId) {
		List<shipmentsBean> shipmentList = new ArrayList<shipmentsBean>();
		String sql = "SELECT * FROM Shipments where order_id=?";

		try (Connection conn = JDBCUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, orderId);

			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					Integer shipmentId = rs.getInt("shipment_id");
					orderId = rs.getInt("order_id");
					String shippingMethod = rs.getString("shipping_method");
					Integer shippingFee = rs.getInt("shipping_fee");
					String trackingNumber = rs.getString("tracking_number");
					Date shippedAt = rs.getTimestamp("shipped_at");
					Date deliveredAt = rs.getTimestamp("delivered_at");
					String status = rs.getString("status");
					String recipientName = rs.getString("recipient_name");
					String recipientPhone = rs.getString("recipient_phone");
					String shippingAddress = rs.getString("shipping_address");
					shipmentsBean shipmentsBean = new shipmentsBean(shipmentId, orderId, shippingMethod, shippingFee,
							trackingNumber, shippedAt, deliveredAt, status, recipientName, recipientPhone,
							shippingAddress);
					shipmentList.add(shipmentsBean);
				}

			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return shipmentList;

	}

	public List<shipmentsBean> findShipmentsId(Integer shipmentId) {
		List<shipmentsBean> shipmentList = new ArrayList<shipmentsBean>();
		String sql = "SELECT * FROM Shipments where shipment_id=?";

		try (Connection conn = JDBCUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, shipmentId);
			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					shipmentId = rs.getInt("shipment_id");
					Integer orderId = rs.getInt("order_id");
					String shippingMethod = rs.getString("shipping_method");
					Integer shippingFee = rs.getInt("shipping_fee");
					String trackingNumber = rs.getString("tracking_number");
					Date shippedAt = rs.getTimestamp("shipped_at");
					Date deliveredAt = rs.getTimestamp("delivered_at");
					String status = rs.getString("status");
					String recipientName = rs.getString("recipient_name");
					String recipientPhone = rs.getString("recipient_phone");
					String shippingAddress = rs.getString("shipping_address");
					shipmentsBean shipmentsBean = new shipmentsBean(shipmentId, orderId, shippingMethod, shippingFee,
							trackingNumber, shippedAt, deliveredAt, status, recipientName, recipientPhone,
							shippingAddress);
					shipmentList.add(shipmentsBean);

				}

			} catch (SQLException e) {
				// TODO: handle exception
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return shipmentList;

	}

	public void changeShipment(String trackingNumber, Date shippedAt, Date deliveredAt, String status, Integer id) {
		String sql = "update Shipments set tracking_number=?, shipped_at=?,delivered_at=?, status=? where order_id=?";
		try (Connection conn = JDBCUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			if (trackingNumber != null && !trackingNumber.isEmpty()) {
				ps.setString(1, trackingNumber);
			} else {
				ps.setNull(1, java.sql.Types.NVARCHAR);
			}

			// 轉換 java.util.Date → java.sql.Date（允許 null）
			if (shippedAt != null) {
				ps.setDate(2, new java.sql.Date(shippedAt.getTime()));
			} else {
				ps.setNull(2, java.sql.Types.DATE);
			}

			if (deliveredAt != null) {
				ps.setDate(3, new java.sql.Date(deliveredAt.getTime()));
			} else {
				ps.setNull(3, java.sql.Types.DATE);
			}
			ps.setString(4, status);
			ps.setInt(5, id);

			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void changeShipmentId(String trackingNumber, Date shippedAt, Date deliveredAt, String status,
			Integer shipmentId) {
		String sql = "update Shipments set tracking_number=?, shipped_at=?,delivered_at=?, status=? where shipment_id=?";
		try (Connection conn = JDBCUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			if (trackingNumber != null && !trackingNumber.isEmpty()) {
				ps.setString(1, trackingNumber);
			} else {
				ps.setNull(1, java.sql.Types.NVARCHAR);
			}

			// 轉換 java.util.Date → java.sql.Date（允許 null）
			if (shippedAt != null) {
				ps.setDate(2, new java.sql.Date(shippedAt.getTime()));
			} else {
				ps.setNull(2, java.sql.Types.DATE);
			}

			if (deliveredAt != null) {
				ps.setDate(3, new java.sql.Date(deliveredAt.getTime()));
			} else {
				ps.setNull(3, java.sql.Types.DATE);
			}
			ps.setString(4, status);
			ps.setInt(5, shipmentId);

			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
