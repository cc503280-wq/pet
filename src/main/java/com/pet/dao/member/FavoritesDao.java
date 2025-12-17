package com.pet.dao.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.pet.model.member.Favorites;

public class FavoritesDao {
	//連線
	private Connection getConnection() throws SQLException, NamingException {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/petDB");
        return dataSource.getConnection();
    }
	
	//查詢全部
	public List<Favorites> queryAllFavorites(){
		
		String sql = "SELECT * FROM favorites_products_view";
		List<Favorites> favorites = new ArrayList<>();
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			
			while(resultSet.next()) {
				Favorites favorite = new Favorites();
				favorite.setFavoriteId(resultSet.getInt("favorite_id"));
				favorite.setMemberId(resultSet.getInt("member_id")); 
				favorite.setProductId(resultSet.getInt("product_id"));
				favorite.setProductName(resultSet.getString("product_name"));
				favorite.setPrice(resultSet.getDouble("price"));
				
				favorites.add(favorite);
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return favorites;
	}
	
	//依productId查詢
	public List<Favorites> queryFavoritesByProductId(int id) {
		String sql = "SELECT * FROM favorites_products_view WHERE product_id=?";
		List<Favorites> favorites = new ArrayList<>();
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while(resultSet.next()) {
					Favorites favorite = new Favorites();
					favorite.setFavoriteId(resultSet.getInt("favorite_id"));
					favorite.setMemberId(resultSet.getInt("member_id")); 
					favorite.setProductId(resultSet.getInt("product_id"));
					favorite.setProductName(resultSet.getString("product_name"));
					favorite.setPrice(resultSet.getDouble("price"));
					
					favorites.add(favorite);
				}
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return favorites;
	}
	//依memberId查詢
	public List<Favorites> queryFavotitesByMemberId(int id) {
		String sql = "SELECT * FROM favorites_products_view WHERE member_id=?";
		List<Favorites> favorites = new ArrayList<>();
		
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while(resultSet.next()) {
					Favorites favorite = new Favorites();
					favorite.setFavoriteId(resultSet.getInt("favorite_id"));
					favorite.setMemberId(resultSet.getInt("member_id")); 
					favorite.setProductId(resultSet.getInt("product_id"));
					favorite.setProductName(resultSet.getString("product_name"));
					favorite.setPrice(resultSet.getDouble("price"));
					
					favorites.add(favorite);
				}
			}
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return favorites;
	}

}
