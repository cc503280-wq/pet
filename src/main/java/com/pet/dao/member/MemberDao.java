package com.pet.dao.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import com.pet.model.member.Member;

public class MemberDao {
	private Session session;
	
	public MemberDao(Session session) {
		this.session = session;
	}
	//查詢全部
	public List<Member> queryAllMembers(){
		
		String hql = "select new com.pet.model.member.Member(" +
	             "m.memberId, m.email, m.name, m.gender, m.birthday, " +
	             "m.phone, m.address, m.picture, m.status, m.points) " +
	             "from Member m order by m.memberId";

		return session.createQuery(hql, Member.class).list();
	}
	
	//查詢啟用中會員
		public List<Member> queryActiveMembers(){
			
			String hql = "select new com.pet.model.member.Member(" +
	                   "m.memberId, m.email, m.name, m.gender, m.birthday, " +
	                   "m.phone, m.address, m.picture, m.status, m.points) " +
	                   "from Member m where m.status = 'active' order by m.memberId";

			return session.createQuery(hql, Member.class).list();
		}
	
		//查詢停用會員
		public List<Member> queryDisabledMembers(){
			
			String hql = "select new com.pet.model.member.Member(" +
                    "m.memberId, m.email, m.name, m.gender, m.birthday, " +
                    "m.phone, m.address, m.picture, m.status, m.points) " +
                    "from Member m where m.status = 'disabled' order by m.memberId";

			return session.createQuery(hql, Member.class).list();
		}
		
	//依ID查詢
	public Member queryMemberById(int id) {
		String hql = "select new com.pet.model.member.Member(" +
                "m.memberId, m.email, m.name, m.gender, m.birthday, " +
                "m.phone, m.address, m.picture, m.status, m.points) " +
                "from Member m where m.memberId = :id";

	    Query<Member> query = session.createQuery(hql, Member.class);
	    query.setParameter("id", id);
	
	    return query.uniqueResult();
	}
	
	//依姓名模糊查詢
	public List<Member> queryMembersByName(String name){
		String hql = "select new com.pet.model.member.Member(" +
                "m.memberId, m.email, m.name, m.gender, m.birthday, " +
                "m.phone, m.address, m.picture) " +
                "from Member m where m.name like :name order by m.memberId";

	    Query<Member> query = session.createQuery(hql, Member.class);
	    query.setParameter("name", "%" + name + "%");
	    return query.list();
    }
	
	// 切換停用/啟用(軟刪除)
	public boolean toggleStatusMember(int id) {
		Member member = session.find(Member.class, id);
        if (member == null) return false;

        String newStatus = "active".equals(member.getStatus()) ? "disabled" : "active";
        member.setStatus(newStatus);
        session.merge(member);
        return true;
	}
	 
	//新增
	public Member createMember(Member member) {
		String hashedPassword = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());
        member.setPassword(hashedPassword);

        session.persist(member);
        return member;
	}
	//修改
	public Member updateMember(Member input) {
		Member member = session.find(Member.class, input.getMemberId());
        if (member == null) return null;

        member.setEmail(input.getEmail());
        member.setName(input.getName());
        member.setGender(input.getGender());
        member.setBirthday(input.getBirthday());
        member.setPhone(input.getPhone());
        member.setAddress(input.getAddress());
        member.setPicture(input.getPicture());

        return member;
    }
	
	
}