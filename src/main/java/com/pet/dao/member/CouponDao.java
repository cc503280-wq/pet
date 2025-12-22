package com.pet.dao.member;

import java.util.Date;
import java.util.List;


import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.member.Coupon;

public class CouponDao {
	private Session session;
	
	public CouponDao(Session session) {
		this.session = session;
	}
	//查詢全部
	public List<Coupon> queryAllCoupons(){
		
		String hql = "from Coupon c order by c.couponId";
        Query<Coupon> query = session.createQuery(hql, Coupon.class);
        return query.list();
	}
	
	//查詢啟用中
		public List<Coupon> queryActiveCoupons(){
			
		String hql = "from Coupon c where c.status = 'active' order by c.couponId";
        Query<Coupon> query = session.createQuery(hql, Coupon.class);
        return query.list();
	}
	
	//查詢停用
		public List<Coupon> queryDisabledCoupons(){
			
			String hql = "from Coupon c where c.status = 'disabled' order by c.couponId";
	        Query<Coupon> query = session.createQuery(hql, Coupon.class);
	        return query.list();
		}
		
	//依ID查詢
	public Coupon queryCouponById(int id) {
		return session.find(Coupon.class, id);
	}
	
	//依發放期間查詢
	public List<Coupon> queryCouponsByIssueRange(Date start, Date end){
		String hql = "from Coupon c " +
                "where c.issueStartAt <= :end " +
                "and c.issueEndAt >= :start";

	    Query<Coupon> query = session.createQuery(hql, Coupon.class);
	    query.setParameter("start", start);
	    query.setParameter("end", end);
	    return query.list();
	}
	

	//依使用期間查詢
	public List<Coupon> queryCouponsByUseRange(Date start, Date end){
		String hql = "from Coupon c " +
                "where c.useStartAt <= :end " +
                "and c.useEndAt >= :start";

	    Query<Coupon> query = session.createQuery(hql, Coupon.class);
	    query.setParameter("start", start);
	    query.setParameter("end", end);
	    return query.list();
	}

	
	// 切換停用/啟用
	public boolean toggleStatusCoupon(int id) {
		Coupon coupon = session.find(Coupon.class, id);
        if (coupon == null) return false;

        String newStatus = "active".equals(coupon.getStatus())
                ? "disabled"
                : "active";

        coupon.setStatus(newStatus);
        session.merge(coupon);
        return true;
	}
	 
	//新增
	public Coupon createCoupon(Coupon coupon) {
	    
		session.persist(coupon);
	    return coupon;
	}
	//修改
	public Coupon updateCoupon(Coupon input) {
		Coupon coupon = session.find(Coupon.class, input.getCouponId());
        if (coupon == null) return null;

        coupon.setCode(input.getCode());
        coupon.setDiscountType(input.getDiscountType());
        coupon.setDiscountValue(input.getDiscountValue());
        coupon.setIsLimited(input.getIsLimited());
        coupon.setTotalAmount(input.getTotalAmount());
        coupon.setIssuedAmount(input.getIssuedAmount());
        coupon.setIssueStartAt(input.getIssueStartAt());
        coupon.setIssueEndAt(input.getIssueEndAt());
        coupon.setUseStartAt(input.getUseStartAt());
        coupon.setUseEndAt(input.getUseEndAt());
        coupon.setMinPurchase(input.getMinPurchase());

        // status 有值才改（避免被覆蓋成 null）
        if (input.getStatus() != null) {
            coupon.setStatus(input.getStatus());
        }

        return coupon;
    }
	
	
}
