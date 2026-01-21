package com.pet.controller.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.member.Member;
import com.pet.model.member.MemberPet;
import com.pet.service.member.MemberPetService;
import com.pet.util.LoginUser;

@RestController
@RequestMapping("/shop/pets")
public class ShopMemberPetController {

	@Autowired
    private MemberPetService memberPetService;

    
    //取得目前登入者的寵物清單
    @GetMapping
    public ResponseEntity<?> getMyPets(@LoginUser Integer userId) {
        if (userId == null) {
        	return ResponseEntity.status(401).body("未登入");
        }
        return ResponseEntity.ok(memberPetService.getPetsByMemberId(userId));
    }

    //新增寵物
    @PostMapping
    public ResponseEntity<?> addPet(@LoginUser Integer userId, @RequestBody MemberPet pet) {
        if (userId == null) {
        	return ResponseEntity.status(401).body("未登入");
        }

        // 關聯目前登入的會員
        Member member = new Member();
        member.setMemberId(userId);
        pet.setMember(member);

        memberPetService.savePet(pet);
        return ResponseEntity.ok("新增成功");
    }

    //修改寵物
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@LoginUser Integer userId, @PathVariable Integer id, @RequestBody MemberPet pet) {
    	if (userId == null) {
        	return ResponseEntity.status(401).body("未登入");
        }
        
        MemberPet existingPet = memberPetService.getPetById(id);
        if (existingPet == null) {
        	return ResponseEntity.status(404).body("找不到寵物");
        }
        
        // 安全檢查：確保這隻寵物真的屬於這位會員（防止越權修改）
        if (!existingPet.getMember().getMemberId().equals(userId)) {
           return ResponseEntity.status(403).body("您無權限修改此寵物");
        }

        existingPet.setPetName(pet.getPetName());
        existingPet.setPetType(pet.getPetType());
        existingPet.setPetBreed(pet.getPetBreed());
        existingPet.setPetAge(pet.getPetAge());
        existingPet.setPetSize(pet.getPetSize());

        memberPetService.savePet(existingPet);
        return ResponseEntity.ok("修改成功");
    }

    //刪除寵物
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@LoginUser Integer userId, @PathVariable Integer id) {
        if (userId == null) {
        	return ResponseEntity.status(401).body("未登入");
        }

        MemberPet existingPet = memberPetService.getPetById(id);
        if (existingPet == null) {
        	return ResponseEntity.status(404).body("找不到寵物");
        }

        if (!existingPet.getMember().getMemberId().equals(userId)) {
            return ResponseEntity.status(403).body("您無權限刪除此寵物");
        }

        memberPetService.deletePet(id);
        return ResponseEntity.ok("刪除成功");
    }
}
