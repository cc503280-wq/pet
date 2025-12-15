package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.ProductBean;
//import connectionPool.HikariCpUtils;

public class GetOneProduct {
	Connection conn;

	public ProductBean getProduct(String id) {
		
		String sql = "SELECT * FROM products p INNER JOIN categories c ON p.category_id = c.category_id WHERE product_id=?";
//		List<ProductBean> products = new ArrayList<ProductBean>();
		ProductBean product = new ProductBean();
		try {
			Context context = new InitialContext();
			DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
			conn = ds.getConnection();
//			conn = HikariCpUtils.getDataSource().getConnection();    //HikariCp
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				product.setProductId(Integer.parseInt(id));
				product.setProductName(rs.getString("product_name"));
				product.setDescription(rs.getString("description"));
				product.setPrice(rs.getDouble("price"));
				product.setStock(rs.getInt("stock"));
				product.setCategoryId(rs.getInt("category_id"));
				product.setCategoryName(rs.getString("category_name"));
				product.setImageUrl(rs.getString("image_url"));
				product.setExpireDate(rs.getString("expire_date"));
				product.setCreatedAt(rs.getDate("created_at"));
				product.setUpdatedAt(rs.getDate("updated_at"));
			}
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
