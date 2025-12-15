package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import bean.CategoriesBean;
//import connectionPool.HikariCpUtils;

public class GetAllCategories {
	Connection conn;

	public List<CategoriesBean> getCategory() {
		String sql = "SELECT * FROM categories";
		List<CategoriesBean> categories = new ArrayList<CategoriesBean>();
		try {
			Context context = new InitialContext();
			DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
			conn = ds.getConnection();
//			conn = HikariCpUtils.getDataSource().getConnection();    //HikariCp
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			CategoriesBean category = null;
			while (rs.next()) {
				category = new CategoriesBean();
				category.setCategory_id(rs.getInt("category_id"));
				category.setCategory_name(rs.getString("category_name"));
				categories.add(category);
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
		return categories;
	}
}
