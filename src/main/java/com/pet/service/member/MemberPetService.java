package com.pet.service.member;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dao.member.MemberPetRepository;
import com.pet.dto.member.PetStatsDTO;
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
    
    /**
     * 根據前端傳來的 type 參數，回傳對應的寵物統計數據
     */
    public PetStatsDTO getPetStats(String type) {
        // 1. 處理堆疊長條圖 (種類 + 體型)
        if ("stacked".equals(type)) {
            List<Object[]> rawData = memberPetRepository.countTypeAndSizeGrouped();
            List<String> labels = new ArrayList<>();
            List<Long> data = new ArrayList<>();

            for (Object[] row : rawData) {
                // 將 種類 與 體型 用逗號合併成標籤，交給前端拆解
                // row[0]=種類, row[1]=體型, row[2]=數量
                labels.add(row[0] + "," + row[1]);
                data.add(((Number) row[2]).longValue());
            }
            return new PetStatsDTO(labels, data);
        }

        // 2. 處理年齡分布 (圓餅圖：包含全體與特定種類連動)
        // 此時 type 可能是 null (全體) 或 "狗"、"貓" (特定種類)
        List<Object[]> rawData = memberPetRepository.countAgeStatsBySpecificType(type);
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        if (rawData != null) {
            for (Object[] row : rawData) {
                labels.add(row[0] != null ? String.valueOf(row[0]) : "未知");
                data.add(row[1] != null ? ((Number) row[1]).longValue() : 0L);
            }
        }

        return new PetStatsDTO(labels, data);
    }
}
