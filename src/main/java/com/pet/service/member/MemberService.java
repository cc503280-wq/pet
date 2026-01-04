package com.pet.service.member;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dao.member.MemberRepository;
import com.pet.model.member.Member;


@Service
@Transactional
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;
	
	public List<Member> getAllMembers() {
        return memberRepository.findAllByOrderByMemberIdAsc();
    }

    public List<Member> getMembersByStatus(String status) {
        return memberRepository.findByStatusOrderByMemberIdAsc(status);
    }

    public Member getMemberById(Integer id) {
        return memberRepository.findById(id).orElse(null);
    }

    public List<Member> getMembersByName(String name) {
        return memberRepository.findByNameContainingOrderByMemberIdAsc(name);
    }

    public Member createMember(Member input) {
        // 1. 處理密碼加密
        String hashedPassword = null;
        if (input.getPassword() != null && !input.getPassword().isEmpty()) {
            hashedPassword = BCrypt.hashpw(input.getPassword(), BCrypt.gensalt());
        }

        // 2. 使用 Builder 組裝乾淨的物件
        Member newMember = Member.builder()
                .name(input.getName())
                .email(input.getEmail())
                .password(hashedPassword)
                .phone(input.getPhone())
                .address(input.getAddress())
                .gender(input.getGender())
                .birthday(input.getBirthday())
                // 安全防護：註冊時點數(points)一律從 0 開始，不管前端傳什麼
                .points(0)
                // 狀態防護：預設為一般會員
                .status("active")
                .build();

        return memberRepository.save(newMember);
    }

    public Member updateMember(Member input) {
        Member member = memberRepository.findById(input.getMemberId()).orElse(null);
        if (member != null) {
            member.setEmail(input.getEmail());
            member.setName(input.getName());
            member.setGender(input.getGender());
            member.setBirthday(input.getBirthday());
            member.setPhone(input.getPhone());
            member.setAddress(input.getAddress());
            // 如果有傳新照片才更新路徑
            if (input.getPicture() != null) {
                member.setPicture(input.getPicture());
            }
            return member;
        }
        return null;
    }

    public boolean toggleStatus(Integer id) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member != null) {
            if ("active".equals(member.getStatus())) {
                member.setStatus("disabled");
            } else {
                member.setStatus("active");
            }
            return true;
        }
        return false;
    }
}
