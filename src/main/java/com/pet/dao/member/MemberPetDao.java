package com.pet.dao.member;

import java.util.List;



import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.member.MemberPet;

public class MemberPetDao {

	private Session session;
	
	public MemberPetDao(Session session) {
		this.session = session;
	}
	
	//查詢全部
	public List<MemberPet> queryAllMemberPets(){
		
		String hql = "select new com.pet.model.member.MemberPet(p.petId, p.memberId, p.petName, p.petType, p.petBreed, p.petAge, p.petSize) " +
                "from MemberPet p order by p.petId";
		Query<MemberPet> query = session.createQuery(hql, MemberPet.class);
		return query.list();
	}
	
	//依petId查詢
	public MemberPet queryMemberPetByPetId(int id) {
		
		String hql = "select new com.pet.model.member.MemberPet(p.petId, p.memberId, p.petName, p.petType, p.petBreed, p.petAge, p.petSize) " +
                "from MemberPet p where p.petId = :id";
		Query<MemberPet> query = session.createQuery(hql, MemberPet.class);
		query.setParameter("id", id);
		return query.uniqueResult(); // 回傳單筆資料
	}
	//依memberId查詢
	public List<MemberPet> queryMemberPetByMemberId(int id) {
		
		String hql = "select new com.pet.model.member.MemberPet(p.petId, p.memberId, p.petName, p.petType, p.petBreed, p.petAge, p.petSize) " +
                "from MemberPet p where p.memberId = :id order by p.petId";
		Query<MemberPet> query = session.createQuery(hql, MemberPet.class);
		query.setParameter("id", id);
		return query.list();
	}
	
	public List<MemberPet> queryPetsByConditions(String type, String age, String size){
		
		StringBuilder hql = new StringBuilder(
		        "select new com.pet.model.member.MemberPet(p.petId, p.memberId, p.petName, p.petType, p.petBreed, p.petAge, p.petSize) " +
		        "from MemberPet p where 1=1"
		    );
		
		if(type!=null && !type.isEmpty()) {
			hql.append("and p.petType = :type");
		}
		if (age != null && !age.isEmpty()) {
            hql.append(" and p.petAge = :age");
        }
        if (size != null && !size.isEmpty()) {
            hql.append(" and p.petSize = :size");
        }

        Query<MemberPet> query = session.createQuery(hql.toString(), MemberPet.class);
        
        if (type != null && !type.isEmpty()) {
            query.setParameter("type", type);
        }
        if (age != null && !age.isEmpty()) {
            query.setParameter("age", age);
        }
        if (size != null && !size.isEmpty()) {
            query.setParameter("size", size);
        }

        return query.list();
	}
	
}
