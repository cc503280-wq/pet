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

	public void usedcoupon(Integer couponId, Integer memberId) {

	    Session session = HibernateUtil.getSessionFactory().getCurrentSession();

	    try {
	        session.beginTransaction();

	        String hql = """
	            update CouponUsersBean cu
	            set cu.status = :status,
	                cu.usedAt = :usedAt
	            where cu.id.couponId = :couponId
	              and cu.id.memberId = :memberId
	        """;

	        Query<?> query = session.createQuery(hql);
	        query.setParameter("status", "used");
	        query.setParameter("usedAt", LocalDateTime.now());
	        query.setParameter("couponId", couponId);
	        query.setParameter("memberId", memberId);

	        query.executeUpdate();

	        session.getTransaction().commit();

	    } catch (Exception e) {
	        if (session.getTransaction().isActive()) {
	            session.getTransaction().rollback();
	        }
	        e.printStackTrace();
	    }
	}

}
