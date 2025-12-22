package com.pet.dao.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.order.orderBean;
import com.pet.model.order.orderItemBean;
import com.pet.utils.HibernateUtil;

public class OrderItemsDao {
	 
	  public boolean insertOrderItem(orderBean order, Integer productId, Integer quantity, BigDecimal unitPrice, BigDecimal subtotal) {
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

	        try {
	            session.beginTransaction();

	            orderItemBean item = new orderItemBean();
	            item.setOrder(order);
	            item.setProductId(productId);
	            item.setQuantity(quantity);
	            item.setUnitPrice(unitPrice.doubleValue());
	            item.setSubtotal(subtotal.doubleValue());

	            session.persist(item); // Hibernate 自動插入
	            session.getTransaction().commit();
	            return true;

	        } catch (Exception e) {
	            if (session.getTransaction().isActive()) session.getTransaction().rollback();
	            e.printStackTrace();
	            return false;
	        }
	    }
	    
	  public List<orderItemBean> findAllOrderItems() {
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        List<orderItemBean> items = new ArrayList<>();

	        try {
	            session.beginTransaction();

	            Query<orderItemBean> query = session.createQuery("from orderItemBean", orderItemBean.class);
	            items = query.getResultList();

	            session.getTransaction().commit();

	        } catch (Exception e) {
	            if (session.getTransaction().isActive()) session.getTransaction().rollback();
	            e.printStackTrace();
	        }

	        return items;
	    }
	  
	  public List<orderItemBean> findOrderItems(orderBean order) {
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        List<orderItemBean> items = new ArrayList<>();

	        try {
	            session.beginTransaction();

	            Query<orderItemBean> query = session.createQuery("from orderItemBean i where i.order = :order", orderItemBean.class);
	            query.setParameter("order", order);
	            items = query.getResultList();

	            session.getTransaction().commit();

	        } catch (Exception e) {
	            if (session.getTransaction().isActive()) session.getTransaction().rollback();
	            e.printStackTrace();
	        }

	        return items;
	    }
}
