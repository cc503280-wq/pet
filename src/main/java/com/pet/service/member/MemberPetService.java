package com.pet.service.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dao.member.MemberPetRepository;
import com.pet.model.member.MemberPet;

@Service
public class MemberPetService {

	@Autowired
	private MemberPetRepository memberPetRepository;
	
	// 查詢全部
    public List<MemberPet> getAllPets() {
        return memberPetRepository.findAllByOrderByPetIdAsc();
    }

    // 依 petId 查詢單筆
    public MemberPet getPetById(Integer petId) {
        return memberPetRepository.findById(petId).orElse(null);
    }

    // 依 memberId 查詢
    public List<MemberPet> getPetsByMemberId(Integer memberId) {
        return memberPetRepository.findByMember_MemberIdOrderByPetIdAsc(memberId);
    }

    // 多條件搜尋
    public List<MemberPet> getPetsByConditions(String type, String age, String size) {
        return memberPetRepository.findPetsByConditions(type, age, size);
    }
}
