package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

//import connectionPool.HikariCpUtils;



public class Actived {
	Connection conn;
	public void SetActived(String id) {
		String sql = "UPDATE products SET is_active=1 WHERE product_id=?";
		try {
			Context context = new InitialContext();
			DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
			conn = ds.getConnection();
//			conn = HikariCpUtils.getDataSource().getConnection();    //HikariCp
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, id);
			stmt.execute();
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
		
	}
}
