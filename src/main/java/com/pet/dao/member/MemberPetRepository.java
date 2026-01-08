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
			+ "(:type IS NULL OR p.petType = :type) AND "
			+ "(:age IS NULL OR p.petAge = :age) AND "
			+ "(:size IS NULL OR p.petSize = :size) "
			+ "ORDER BY p.petId")
	List<MemberPet> findPetsByConditions(@Param("type") String type, @Param("age") String age, @Param("size") String size);
	
	//預約
	List<MemberPet> findByMemberMemberId(Integer memberId);
}
