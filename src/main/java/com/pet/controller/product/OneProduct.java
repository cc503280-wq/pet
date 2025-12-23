package com.pet.controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.pet.model.product.ProductBean;
import com.pet.utils.HibernateUtil;

@WebServlet("/OneProduct")
public class OneProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("product_id");
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.getCurrentSession();
		
		ProductBean product = session.find(ProductBean.class, Integer.parseInt(id));
		
		request.setAttribute("product", product);
		
		request.getRequestDispatcher("/admin/layout/OneProduct.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
