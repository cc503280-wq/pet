package com.pet.controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.pet.dao.product.ProductDao;
import com.pet.model.product.ProductBean;
import com.pet.utils.HibernateUtil;

@WebServlet("/UpdateProductDone")
@MultipartConfig(location = "C:/images/")
public class UpdateProductDone extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.getCurrentSession();

		String oldImageName = request.getParameter("old_image_name");
		Part part = request.getPart("photo");
		String newFilename = part.getSubmittedFileName();
		String finalImageName;
		
		if (newFilename != null && !newFilename.isEmpty() && part.getSize() > 0) {
	        finalImageName = newFilename;
	        part.write(finalImageName); 
	    } else {
	        if (oldImageName != null && !oldImageName.isEmpty()) {
	            finalImageName = oldImageName;
	        } else {
	            finalImageName = ""; 
	        }
	    }
		
		
		String id = request.getParameter("id");
		String pname = request.getParameter("pname");
		String pdes = request.getParameter("pdes");
		String price = request.getParameter("price");
		String stock = request.getParameter("stock");
		String value = request.getParameter("category");
		String image = finalImageName;
		String[] parts = value.split(",");
		String categoryid = parts[0];
		String categoryname = parts[1];
		String expiredate = request.getParameter("expiredate");
		String is_acitve = request.getParameter("is_acitve");

		ProductDao pDao = new ProductDao(session);
		ProductBean product = pDao.updateProduct(pname, pdes, price, stock, categoryid, image, categoryname, expiredate,
				is_acitve, id);
		if (product != null) {
	        Session session2 = factory.getCurrentSession(); 
	        ProductDao pDao2 = new ProductDao(session2);
	        pDao2.setImage(product);
	        
	        Session session3 = factory.getCurrentSession();
			session3.beginTransaction();
			ProductBean productForJsp = session3.get(ProductBean.class, product.getProductId());

			if (productForJsp != null && productForJsp.getCategory() != null) {
				productForJsp.getCategory().getCategoryName();
			}

			session3.getTransaction().commit();

			request.setAttribute("product", productForJsp);
	    }
		request.getRequestDispatcher("/admin/layout/UpdateProductDone.jsp").forward(request, response);
	}

}
