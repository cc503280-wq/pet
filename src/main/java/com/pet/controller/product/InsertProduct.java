package com.pet.controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

//import java.io.File;
import java.io.IOException;
import java.util.HashMap;
//import java.nio.file.Files;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.pet.dao.product.ProductDao;
import com.pet.model.product.ProductBean;
import com.pet.utils.HibernateUtil;

@WebServlet("/InsertProduct")
@MultipartConfig(location = "C:/images/")
public class InsertProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> errorMsgs = new HashMap<>();
	    request.setAttribute("errorMsgs", errorMsgs);
		

		Part part = request.getPart("photo");
		String filename = part.getSubmittedFileName();
		part.write(filename);

		String pname = request.getParameter("pname");
		String pdes = request.getParameter("pdes");
		String price = request.getParameter("price");
		String stock = request.getParameter("stock");
		String value = request.getParameter("category");
		String[] parts = value.split(",");
		String categoryid = parts[0];
		String categoryname = parts[1];
		String expiredate = request.getParameter("expiredate");
		String is_acitve = request.getParameter("is_acitve");
		String imageurl = filename;

		// 驗證：商品名稱
	    if (pname == null || pname.trim().isEmpty()) {
	        errorMsgs.put("pname", "商品名稱請勿留空");
	    }
	    
	    // 驗證：價格 (檢查是否為空，也可以順便檢查是不是數字)
	    if (price == null || price.trim().isEmpty()) {
	        errorMsgs.put("price", "價格請勿留空");
	    } else {
	        try {
	            Double.parseDouble(price); // 試著轉轉看，轉不過代表不是數字
	        } catch (NumberFormatException e) {
	            errorMsgs.put("price", "價格必須是數字");
	        }
	    }

	    // 驗證：庫存
	    if (stock == null || stock.trim().isEmpty()) {
	        errorMsgs.put("stock", "庫存請勿留空");
	    }
	    
	    if (!errorMsgs.isEmpty()) {
	        request.setAttribute("pname", pname);
	        request.setAttribute("pdes", pdes);
	        request.setAttribute("price", price);
	        request.setAttribute("stock", stock);
	        request.getRequestDispatcher("/admin/layout/InsertProduct.jsp").forward(request, response);
	        return; 
	    }
		
		
		// 新增商品
		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session1 = factory.getCurrentSession();
		ProductDao pDao = new ProductDao(session1);
		ProductBean product = pDao.addProduct(pname, pdes, price, stock, categoryid, imageurl, categoryname, expiredate,
				is_acitve);

		// 新增圖片
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
		request.getRequestDispatcher("/admin/layout/InsertProductDone.jsp").forward(request, response);

	}
}
