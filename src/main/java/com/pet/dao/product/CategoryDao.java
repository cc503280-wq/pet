package com.pet.dao.product;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.product.CategoriesBean;


public class CategoryDao {
	private Session session;

    public CategoryDao(Session session) {
        this.session = session;
    }

    // 取得所有分類
    public List<CategoriesBean> getCategories() {
        List<CategoriesBean> categories = new ArrayList<>();

        try {
            session.beginTransaction();

            String hql = "FROM CategoriesBean";

            Query<CategoriesBean> query = session.createQuery(hql, CategoriesBean.class);
            categories = query.getResultList();

            session.getTransaction().commit();

        } catch (Exception e) {
            // 發生錯誤時回滾
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
        }

        return categories;
    }
}
