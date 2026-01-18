package com.pet.controller.member;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pet.dto.member.RegistrationStatsDTO;
import com.pet.model.member.Member;
import com.pet.service.member.MemberService;

@RestController
@RequestMapping("/members")
public class MemberController {

	@Autowired
    private MemberService memberService;

    // 查詢 (含狀態篩選)
    @GetMapping
    public List<Member> list(@RequestParam(defaultValue = "all") String status) {
        if ("all".equals(status)) {
            return memberService.getAllMembers();
        }
        return memberService.getMembersByStatus(status);
    }

    // 依 ID 查詢
    @GetMapping("/{id}")
    public Member queryById(@PathVariable Integer id) {
        return memberService.getMemberById(id);
    }

    // 模糊查詢
    @GetMapping("/search")
    public List<Member> queryLikeName(@RequestParam String name) {
        return memberService.getMembersByName(name);
    }

    // 新增
    @PostMapping
    public Member createMember(@ModelAttribute Member member, 
                         @RequestParam(value = "pictureFile", required = false) MultipartFile file) throws IOException {
        return memberService.createMemberWithImage(member, file);
    }

    // 修改
    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Integer id, 
                         @ModelAttribute Member member,
                         @RequestParam(value = "pictureFile", required = false) MultipartFile file) throws IOException {
        member.setMemberId(id);
        return memberService.updateMemberWithImage(id, member, file);
    }

    // 切換狀態
    @PatchMapping("/{id}/toggle")
    public boolean toggleMemberStatus(@PathVariable Integer id) {
        return memberService.toggleStatus(id);
    }
    
    
      //圖表專用 API：獲取會員註冊統計，路徑：GET /members/stats?period=6m
    @GetMapping("/stats")
    public RegistrationStatsDTO getStats(@RequestParam(defaultValue = "6m") String period) {
        return memberService.getMemberRegistrationStats(period);
    }


    
}
