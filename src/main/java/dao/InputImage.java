package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.ProductBean;
//import com.products.connectionPool.HikariCpUtils;

public class InputImage {
	Connection conn;
	public void setimage(ProductBean product) {
		String sql = "{call AddProductImage(?,?)}";
		try {
			Context context = new InitialContext();
			DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
			conn = ds.getConnection();
//			conn = HikariCpUtils.getDataSource().getConnection();    //HikariCp
			CallableStatement cast = conn.prepareCall(sql);
			cast.setInt(1, product.getProductId());
			cast.setString(2, product.getImageUrl());
			cast.execute();		
			cast.close();
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
		
	}
}
