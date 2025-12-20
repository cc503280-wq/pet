package com.pet.dao.member;

import java.util.List;


import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.member.CouponUsers;

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

//	public void usedcoupon(Integer couponId) {
//		String sql = "update coupon_users set status = ? ,used_at=? where coupon_id=?";
//		try (Connection connection = getConnection();
//				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//			preparedStatement.setString(1, "used");
//			preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
//			preparedStatement.setInt(3, couponId);
//			preparedStatement.execute();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NamingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//	}

}
