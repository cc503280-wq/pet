package com.pet.controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.pet.dao.product.CategoryDao;
import com.pet.model.product.CategoriesBean;
import com.pet.model.product.ProductBean;
import com.pet.utils.HibernateUtil;

@WebServlet("/UpdateProduct")
public class UpdateProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.getCurrentSession();
		
		String id = request.getParameter("product_id");
		
		ProductBean product = session.find(ProductBean.class, Integer.parseInt(id));
		request.setAttribute("product", product);
		
		CategoryDao cDao = new CategoryDao(session);
		List<CategoriesBean> categories = cDao.getCategories();
		request.setAttribute("categories", categories);
		
		request.getRequestDispatcher("/admin/layout/UpdateProduct.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
