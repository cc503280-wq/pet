package com.pet.dao.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.order.orderBean;
import com.pet.utils.HibernateUtil;

public class OrderDao {

	private Session session;

	public OrderDao() {
		this.session = HibernateUtil.getSessionFactory().getCurrentSession();
	}

	public orderBean insertOrder(int memberId, Date orderDate, String status, BigDecimal totalAmountUndiscount,
			Integer couponId, BigDecimal totalAmountDiscount, int usePoints, BigDecimal totalAmountDiscountPoints,
			int getPoints) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		orderBean order = new orderBean();
		order.setMemberId(memberId);
		order.setOrderDate(orderDate);
		order.setStatus(status);
		order.setTotalAmountUndiscount(totalAmountUndiscount);
		order.setCouponId(couponId);
		order.setTotalAmountDiscount(totalAmountDiscount);
		order.setUsePoints(usePoints);
		order.setTotalAmountDiscountPoints(totalAmountDiscountPoints);
		order.setGetPoints(getPoints);

		try {
			session.beginTransaction();
			session.persist(order); // Hibernate 自動產生 id
			session.getTransaction().commit();
			return order; // 回傳完整的 orderBean
		} catch (Exception e) {
			if (session.getTransaction().isActive())
				session.getTransaction().rollback();
			e.printStackTrace();
			return null;
		}
	}

	public List<orderBean> findAllOrders() {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		List<orderBean> orderList = new ArrayList<>();

		try {
			session.beginTransaction();

			String hql = "from orderBean"; // Entity 名稱，不是表名
			Query<orderBean> query = session.createQuery(hql, orderBean.class);

			orderList = query.getResultList();

			session.getTransaction().commit();

		} catch (Exception e) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		}

		return orderList;
	}

	public List<orderBean> findIdOrders(Integer memberId) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		List<orderBean> orderList = new ArrayList<>();

		try {
			session.beginTransaction();

			String hql = "from orderBean o where o.memberId = :memberId";
			Query<orderBean> query = session.createQuery(hql, orderBean.class);
			query.setParameter("memberId", memberId);

			orderList = query.getResultList();

			session.getTransaction().commit();

		} catch (Exception e) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		}

		return orderList;
	}

	public void changeOrder(String status, Integer id) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		try {
			session.beginTransaction();

			orderBean order = session.get(orderBean.class, id);
			if (order != null) {
				order.setStatus(status);
				// 不用寫 update，Hibernate 會自動偵測變更
			}

			session.getTransaction().commit();

		} catch (Exception e) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		}
	}
	public orderBean findOrderById(int orderId) {
	    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	    orderBean order = null;
	    try {
	        session.beginTransaction();
	        order = session.get(orderBean.class, orderId);
	        session.getTransaction().commit();
	    } catch(Exception e) {
	        if(session.getTransaction().isActive()) session.getTransaction().rollback();
	        e.printStackTrace();
	    }
	    return order;
	}
}
