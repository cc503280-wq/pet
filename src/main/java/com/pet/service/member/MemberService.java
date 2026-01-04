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

    public Member createMember(Member member) {
        if (member.getPassword() != null && !member.getPassword().isEmpty()) {
            String hashedPassword = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());
            member.setPassword(hashedPassword);
        }
        return memberRepository.save(member);
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
