package com.pet.service.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dao.member.FavoritesRepository;
import com.pet.dao.member.MemberRepository;
import com.pet.model.member.Favorites;

@Service
@Transactional(readOnly = true)
public class FavoritesService {

	@Autowired
	private FavoritesRepository fRepository;
	
	@Autowired
	private MemberRepository mRepository;
	
	//查詢全部
	public List<Favorites> getAllFavorites(){
		return fRepository.findAllByOrderByFavoriteIdAsc();
	}
	
	//依productId查詢
	public List<Favorites> getFavoritesByProductId(Integer productId){
		return fRepository.findByProductIdOrderByFavoriteIdAsc(productId);
	}
	
	//依memberId查詢
	public List<Favorites> getFavoritesByMemberId(Integer memberId){
		if(!mRepository.existsById(memberId)) {
			throw new RuntimeException("找不到編號為"+memberId+"的會員，無法查詢收藏");
		}
		
		return fRepository.findByMemberIdOrderByFavoriteIdAsc(memberId);
	}
}
