package com.pet.dao.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.member.Favorites;

public interface FavoritesRepository extends JpaRepository<Favorites, Integer> {
	//查詢全部
	List<Favorites> findAllByOrderByFavoriteIdAsc();
	
	//依productId查詢
	List<Favorites> findByProductIdOrderByFavoriteIdAsc(Integer productId);
	
	//依memberId查詢
	List<Favorites> findByMemberIdOrderByFavoriteIdAsc(Integer memberId);
}
