package com.pet.controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.pet.dao.product.ProductDao;
import com.pet.utils.HibernateUtil;


@WebServlet("/CanSold")
public class CanSold extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.getCurrentSession();
		String id = request.getParameter("product_id");
		
		ProductDao pDao = new ProductDao(session);
		pDao.SetActived(id); //做上架商品
		response.sendRedirect(request.getContextPath() + "/NotOnShelf");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
