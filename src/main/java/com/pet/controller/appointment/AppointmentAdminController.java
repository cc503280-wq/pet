package com.pet.controller.appointment;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pet.model.member.Member;
import com.pet.service.member.MemberService;

import lombok.RequiredArgsConstructor;

/**
 * AppointmentAdminController: 處理後台預約管理的頁面渲染
 * 使用 @Controller 而非 @RestController，以支援 Thymeleaf 視圖
 */
@Controller
@RequestMapping("/admin/appointments")
@RequiredArgsConstructor
public class AppointmentAdminController {

    private final MemberService memberService;

    /**
     * 顯示「新增預約」的頁面 (後台 Admin - 回傳 HTML)
     */
    @GetMapping("/insert")
    public String showAddPage(Model model) {
        List<Member> memberList = memberService.getAllMembers();
        model.addAttribute("memberList", memberList);
        return "admin/InsertAppointment";
    }
}
