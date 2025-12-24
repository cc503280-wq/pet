package com.pet.dao.member;

import java.util.List;


import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.member.Favorites;

public class FavoritesDao {
	private Session session;
	
	public FavoritesDao(Session session) {
		this.session = session;
	}
	//查詢全部
	public List<Favorites> queryAllFavorites(){
		
		String hql = "select new com.pet.model.member.Favorites(" +
	            "f.favoriteId, f.memberId, f.productId, f.productName, f.price) " +
	            "from Favorites f order by f.favoriteId";

        Query<Favorites> query = session.createQuery(hql, Favorites.class);
        return query.list();
		
	}
	
	//依productId查詢
	public List<Favorites> queryFavoritesByProductId(int id) {
		
		String hql = "select new com.pet.model.member.Favorites(" +
	            "f.favoriteId, f.memberId, f.productId, f.productName, f.price) " +
	            "from Favorites f where f.productId = :id order by f.favoriteId";

	        Query<Favorites> query = session.createQuery(hql, Favorites.class);
	        query.setParameter("id", id);
	        return query.list();
		
	}
	//依memberId查詢
	public List<Favorites> queryFavoritesByMemberId(int id) {
		
		String hql = "select new com.pet.model.member.Favorites(" +
	            "f.favoriteId, f.memberId, f.productId, f.productName, f.price) " +
	            "from Favorites f where f.memberId = :id order by f.favoriteId";

	        Query<Favorites> query = session.createQuery(hql, Favorites.class);
	        query.setParameter("id", id);
	        return query.list();
	}

}
