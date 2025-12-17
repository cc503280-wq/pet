package com.pet.dao.product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.pet.model.product.ProductBean;

public class DoUpdateProduct {
	Connection conn;

	public ProductBean updateProduct(String pname, String pdes, String price, String stock, String categoryid,String image,
			String categoryname, String expiredate, String is_active,String id) {
		String sql = "UPDATE products SET product_name=?,description=?,price=?,stock=?,category_id=?,"
				+ "image_url=?,expire_date=?,is_active=?,updated_at=? WHERE product_id=?";
		String sql2 ="SELECT is_active,updated_at FROM products WHERE product_id=?";
		String sql3 ="UPDATE product_images SET image_url=? WHERE product_id=? AND sort_order=1";
		ProductBean product = new ProductBean();
		try {
			Context context = new InitialContext();
			DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
			conn = ds.getConnection();
			int stockInt = Integer.parseInt(stock);
	        int isActiveInt = Integer.parseInt(is_active);  // "0" or "1"
	        // ★ 核心邏輯：庫存為 0 就強制下架
	        if (stockInt <= 0) {
	            isActiveInt = 0; // 強制下架
	        }
//			conn = HikariCpUtils.getDataSource().getConnection();    //HikariCp
			PreparedStatement stmt = conn.prepareStatement(sql); 	//設定sql的值
			stmt.setString(1, pname);
			stmt.setString(2, pdes);
			stmt.setString(3, price);
			stmt.setString(4, stock);
			stmt.setString(5, categoryid);
			stmt.setString(6, image);
			stmt.setString(7, expiredate);
			stmt.setString(8, String.valueOf(isActiveInt));
			stmt.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));
			stmt.setString(10, id);
			stmt.execute();
			stmt.close();
			
			product.setProductId(Integer.parseInt(id));
			product.setProductName(pname);							//設定回傳product的值
			product.setDescription(pdes);
			product.setPrice(Double.parseDouble(price));
			product.setStock(Integer.parseInt(stock));
			product.setCategoryId(Integer.parseInt(categoryid));
			product.setImageUrl(image);
			product.setCategoryName(categoryname);
			product.setExpireDate(expiredate);
			
			PreparedStatement stmt2 = conn.prepareStatement(sql2);	//獨立取出更新時間
			stmt2.setString(1, id);
			ResultSet rs = stmt2.executeQuery();
			if(rs.next()) {
				product.setIsActive(rs.getBoolean("is_active"));
				product.setUpdatedAt(rs.getDate("updated_at"));			
			}
			stmt2.close();
			
			PreparedStatement stmt3 = conn.prepareStatement(sql3);
			stmt3.setString(1, image);
			stmt3.setString(2, id);
			stmt3.execute();
			stmt3.close();
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return product;
	}
}
