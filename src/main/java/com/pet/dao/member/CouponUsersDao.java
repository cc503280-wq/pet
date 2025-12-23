package com.pet.dao.member;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.member.CouponUsers;
import com.pet.utils.HibernateUtil;

public class CouponUsersDao {
	
	private Session session;
	
	public CouponUsersDao(Session session) {
		this.session = session;
	}
	// 查詢全部
	public List<CouponUsers> queryAllCouponUsers() {

		String hql = "select new com.pet.model.member.CouponUsers(" +
	            "c.id, c.couponId, c.memberId, c.status, c.assignedAt, c.usedAt, c.code, c.discountType, c.discountValue, c.minPurchase, c.issueStartAt, c.issueEndAt, c.useStartAt, c.useEndAt, c.isExpired) " +
	            "from CouponUsers c order by c.id";

        Query<CouponUsers> query = session.createQuery(hql, CouponUsers.class);
        return query.list();
	}

	// 依couponId查詢
	public List<CouponUsers> queryCouponUsersByCouponId(int id) {
		String hql = "select new com.pet.model.member.CouponUsers(" +
				"c.id, c.couponId, c.memberId, c.status, c.assignedAt, c.usedAt, c.code, c.discountType, c.discountValue, c.minPurchase, c.issueStartAt, c.issueEndAt, c.useStartAt, c.useEndAt, c.isExpired) " +
	            "from CouponUsers c where c.couponId = :id order by c.id";

	        Query<CouponUsers> query = session.createQuery(hql, CouponUsers.class);
	        query.setParameter("id", id);
	        return query.list();
	}

	// 依memberId查詢
	public List<CouponUsers> queryCouponUsersByMemberId(int id) {
		String hql = "select new com.pet.model.member.CouponUsers(" +
				"c.id, c.couponId, c.memberId, c.status, c.assignedAt, c.usedAt, c.code, c.discountType, c.discountValue, c.minPurchase, c.issueStartAt, c.issueEndAt, c.useStartAt, c.useEndAt, c.isExpired) " +
	            "from CouponUsers c where c.memberId = :id order by c.id";

	        Query<CouponUsers> query = session.createQuery(hql, CouponUsers.class);
	        query.setParameter("id", id);
	        return query.list();
	}

	public void usedcoupon(Integer couponId) {
		CouponUsers couponUsers = session.find(CouponUsers.class, couponId);
		couponUsers.setStatus("used");
		couponUsers.setUsedAt(Timestamp.valueOf(LocalDateTime.now()));
	}

}
