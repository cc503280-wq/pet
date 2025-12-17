package com.pet.dao.product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.pet.model.product.ProductBean;

public class DoInsertProduct {
	Connection conn;

	public ProductBean setProduct(String pname, String pdes, String price, String stock, String categoryid,
			String imageurl,String categoryname, String expiredate, String is_active) {
		String sql = "INSERT INTO products(product_name, description, price, stock, "
				+ "category_id,image_url, expire_date, is_active)" + "VALUES(?,?,?,?,?,?,?,?)";
//		照片尚未加入
		ProductBean product = new ProductBean();
		try {
			Context context = new InitialContext();
			DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
			conn = ds.getConnection();
//			conn = HikariCpUtils.getDataSource().getConnection();    //HikariCp
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, pname);
			stmt.setString(2, pdes);
			stmt.setString(3, price);
			stmt.setString(4, stock);
			stmt.setString(5, categoryid);
			stmt.setString(6, imageurl);
			stmt.setString(7, expiredate);
			stmt.setString(8, is_active);
			stmt.execute();
			product.setProductName(pname);
			product.setDescription(pdes);
			product.setPrice(Double.parseDouble(price));
			product.setStock(Integer.parseInt(stock));
			product.setCategoryId(Integer.parseInt(categoryid));
			product.setImageUrl(imageurl);
			product.setCategoryName(categoryname);
			product.setExpireDate(expiredate);
			product.setIsActive(!Boolean.parseBoolean(is_active));
			stmt.close();
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
