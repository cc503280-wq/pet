package com.pet.dao.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pet.model.member.MemberPet;

@Repository
public interface MemberPetRepository extends JpaRepository<MemberPet, Integer> {

	//查詢全部
	List<MemberPet> findAllByOrderByPetIdAsc();
	
	//依petId查詢，內建findById
	
	//依memberId查詢
	List<MemberPet> findByMember_MemberIdOrderByPetIdAsc(Integer memberId);
	
	//依條件查詢
	@Query("SELECT p FROM MemberPet p WHERE "
	        + "(:type IS NULL OR :type = '' OR p.petType = :type) AND "
	        + "(:age IS NULL OR :age = '' OR p.petAge = :age) AND "
	        + "(:size IS NULL OR :size = '' OR p.petSize = :size) "
	        + "ORDER BY p.petId")
	List<MemberPet> findPetsByConditions(
	    @Param("type") String type, 
	    @Param("age") String age, 
	    @Param("size") String size);
	
    // 根據種類過濾年齡分布 (用於連動圓餅圖)
    @Query("SELECT p.petAge, COUNT(p) FROM MemberPet p WHERE (:type IS NULL OR p.petType = :type) GROUP BY p.petAge")
    List<Object[]> countAgeStatsBySpecificType(@Param("type") String type);

    //查詢種類跟體型
    @Query("SELECT p.petType, p.petSize, COUNT(p) FROM MemberPet p GROUP BY p.petType, p.petSize")
    List<Object[]> countTypeAndSizeGrouped();
	
	//預約
	List<MemberPet> findByMemberMemberId(Integer memberId);
}
