package com.pet.dao.member;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.member.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {

}
