package com.pet.controller.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pet.dto.member.PetStatsDTO;
import com.pet.model.member.MemberPet;
import com.pet.service.member.MemberPetService;

@RestController
@RequestMapping("/memberPets")
public class MemberPetController {

	@Autowired
	private MemberPetService memberPetService;
	
	// 1. 查詢全部 (GET /memberPets)
    @GetMapping
    public List<MemberPet> list() {
        return memberPetService.getAllPets();
    }

    // 2. 依 petId 查詢單筆 (GET /memberPets/1)
    @GetMapping("/{petId}")
    public MemberPet queryByPetId(@PathVariable Integer petId) {
        return memberPetService.getPetById(petId);
    }

    // 3. 依 memberId 查詢 (GET /memberPets/member/5)
    @GetMapping("/member/{memberId}")
    public List<MemberPet> queryByMemberId(@PathVariable Integer memberId) {
        return memberPetService.getPetsByMemberId(memberId);
    }

    // 4. 多條件搜尋 (GET /memberPets/search?petType=狗&petAge=幼年&petSize=小型)
    @GetMapping("/search")
    public List<MemberPet> search(
            @RequestParam(required = false) String petType,
            @RequestParam(required = false) String petAge,
            @RequestParam(required = false) String petSize) {
        return memberPetService.getPetsByConditions(petType, petAge, petSize);
    }
    
    //寵物圖表分析
    @GetMapping("/stats")
    public PetStatsDTO getStats(@RequestParam(required = false) String type) {
        return memberPetService.getPetStats(type);
    }
}
