package com.pet.dao.order;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.pet.model.order.orderItemBean;
import com.pet.utils.JDBCUtil;

public class OrderItemsDao {
	 
	    public boolean insertOrderItem(
	            Integer productId,
	            Integer orderId,
	            Integer quantity,
	            BigDecimal unitPrice,
	            BigDecimal subtotal
	            
	    ) {
	        // 使用 OUTPUT INSERTED.order_id 取得剛插入的自增欄位
	        String sql = "insert into OrderItems (product_id,order_id,quantity,unit_price,subtotal) values (?,?,?,?,?)";

	        try (Connection connection=JDBCUtil.getConnection();
	        		PreparedStatement ps = connection.prepareStatement(sql)) {
	        	ps.setInt(1, productId);
	            ps.setInt(2, orderId);
	            ps.setInt(3, quantity);
	            ps.setBigDecimal(4, unitPrice);
	            ps.setBigDecimal(5, subtotal);

	            int rows = ps.executeUpdate();
	            return rows > 0; // 插入成功返回 true
	           
	            }

	         catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	            
	        }
	    }
	    
	    public List<orderItemBean> findAllOrderItems() {
			List<orderItemBean> orderitems = new ArrayList<orderItemBean>();
			String sql = "SELECT * FROM OrderItems";
			
			try (Connection connection=JDBCUtil.getConnection();
	        		PreparedStatement ps = connection.prepareStatement(sql);
					ResultSet resultSet=ps.executeQuery();) {
				
				while (resultSet.next()) {
					Integer productItemId = resultSet.getInt("product_item_id");
					Integer orderId = resultSet.getInt("order_id");
					Integer productId = resultSet.getInt("product_id");
					Integer quantity = resultSet.getInt("quantity");
					Double unitPrice = resultSet.getDouble("unit_price");
					Double subtotal = resultSet.getDouble("subtotal");
					orderItemBean orderItem = new orderItemBean(productItemId, orderId, productId, quantity, unitPrice, subtotal);
					orderitems.add(orderItem);
					
				}
				

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return orderitems;

		}
	    public List<orderItemBean> findOrderItems(Integer orderId) {
			List<orderItemBean> orderitems = new ArrayList<orderItemBean>();
			String sql = "SELECT * FROM OrderItems where order_id=?";
			
			try (Connection connection=JDBCUtil.getConnection();
	        		PreparedStatement ps = connection.prepareStatement(sql)) {
				ps.setInt(1,orderId);
				try (ResultSet resultSet=ps.executeQuery();){
					while (resultSet.next()) {
						Integer productItemId = resultSet.getInt("product_item_id");
						orderId = resultSet.getInt("order_id");
						Integer productId = resultSet.getInt("product_id");
						Integer quantity = resultSet.getInt("quantity");
						Double unitPrice = resultSet.getDouble("unit_price");
						Double subtotal = resultSet.getDouble("subtotal");
						orderItemBean orderItem = new orderItemBean(productItemId, orderId, productId, quantity, unitPrice, subtotal);
						orderitems.add(orderItem);
						
					}
					
				} catch (SQLException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				
				

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return orderitems;

		}
}
