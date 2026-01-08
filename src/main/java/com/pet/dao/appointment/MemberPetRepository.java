package com.pet.dao.appointment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.appointment.ServiceItem;
import com.pet.model.member.MemberPet;

/*
 * TODO: 
 * 這是我自己新增的，要放到組長的MemberPetRepository
 */
public interface MemberPetRepository extends JpaRepository<MemberPet, Integer> {
	List<MemberPet> findByMemberMemberId(Integer memberId);
}
