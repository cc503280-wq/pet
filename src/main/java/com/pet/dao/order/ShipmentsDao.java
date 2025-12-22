package com.pet.dao.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.order.orderBean;
import com.pet.model.order.shipmentsBean;
import com.pet.utils.HibernateUtil;

public class ShipmentsDao {

	public boolean insertShipment(orderBean order, String shippingMethod, Integer shippingFee, String recipientName,
			String recipientPhone, String shippingAddress) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		try {
			session.beginTransaction();

			shipmentsBean shipment = new shipmentsBean();
			shipment.setorderId(order); // 關聯 orderBean
			shipment.setShippingMethod(shippingMethod);
			shipment.setShippingFee(shippingFee);
			shipment.setRecipientName(recipientName);
			shipment.setRecipientPhone(recipientPhone);
			shipment.setShippingAddress(shippingAddress);

			session.persist(shipment);
			session.getTransaction().commit();
			return true;

		} catch (Exception e) {
			if (session.getTransaction().isActive())
				session.getTransaction().rollback();
			e.printStackTrace();
			return false;
		}
	}

	public List<shipmentsBean> findAllShipments() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<shipmentsBean> shipments = new ArrayList<>();

        try {
            session.beginTransaction();
            Query<shipmentsBean> query = session.createQuery("from shipmentsBean", shipmentsBean.class);
            shipments = query.getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
        }

        return shipments;
    }
	 public List<shipmentsBean> findOrderShipments(orderBean order) {
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        List<shipmentsBean> shipments = new ArrayList<>();

	        try {
	            session.beginTransaction();
	            Query<shipmentsBean> query = session.createQuery(
	                "from shipmentsBean s where s.orderId = :order", shipmentsBean.class);
	            query.setParameter("order", order);
	            shipments = query.getResultList();
	            session.getTransaction().commit();
	        } catch (Exception e) {
	            if (session.getTransaction().isActive()) session.getTransaction().rollback();
	            e.printStackTrace();
	        }

	        return shipments;
	    }

	 public shipmentsBean findShipmentById(Integer shipmentId) {
		    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		    shipmentsBean shipment = null;

		    try {
		        session.beginTransaction();

		        // 用 session.get 直接根據主鍵查詢
		        shipment = session.get(shipmentsBean.class, shipmentId);

		        session.getTransaction().commit();
		    } catch (Exception e) {
		        if (session.getTransaction().isActive()) session.getTransaction().rollback();
		        e.printStackTrace();
		    }

		    return shipment;
		}

	 public void changeShipment(String trackingNumber, Date shippedAt, Date deliveredAt, String status, Integer orderId) {
		    Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		    try {
		        session.beginTransaction();

		        // 用 HQL 找出 shipment
		        Query<shipmentsBean> query = session.createQuery(
		            "from shipmentsBean s where s.orderId.orderId = :orderId", shipmentsBean.class);
		        query.setParameter("orderId", orderId);
		        shipmentsBean shipment = query.uniqueResult();

		        if (shipment != null) {
		            shipment.setTrackingNumber(trackingNumber);
		            shipment.setShippedAt(shippedAt);
		            shipment.setDeliveredAt(deliveredAt);
		            shipment.setStatus(status);
		            // Hibernate 會自動偵測變更，不需要手動 update
		        }

		        session.getTransaction().commit();
		    } catch (Exception e) {
		        if (session.getTransaction().isActive()) session.getTransaction().rollback();
		        e.printStackTrace();
		    }
		}

	 public void changeShipmentById(String trackingNumber, Date shippedAt, Date deliveredAt, String status, Integer shipmentId) {
		    Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		    try {
		        session.beginTransaction();

		        shipmentsBean shipment = session.get(shipmentsBean.class, shipmentId);
		        if (shipment != null) {
		            shipment.setTrackingNumber(trackingNumber);
		            shipment.setShippedAt(shippedAt);
		            shipment.setDeliveredAt(deliveredAt);
		            shipment.setStatus(status);
		        }

		        session.getTransaction().commit();
		    } catch (Exception e) {
		        if (session.getTransaction().isActive()) session.getTransaction().rollback();
		        e.printStackTrace();
		    }
		}
}
