package com.pet.dao.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pet.model.member.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

	// 查詢全部 
    List<Member> findAllByOrderByMemberIdAsc();

    // 查詢啟用中
    List<Member> findByStatusOrderByMemberIdAsc(String status);

    // 依姓名模糊查詢
    List<Member> findByNameContainingOrderByMemberIdAsc(String name);

}
