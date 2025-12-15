package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import dao.GetAllCategories;
import dao.GetAllProducts;
import bean.CategoriesBean;
import bean.ProductBean;



@WebServlet("/NotOnShelf")
public class NotOnShelf extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		GetAllProducts all = new GetAllProducts();
		List<ProductBean> products = all.NotOnShelf();
		request.setAttribute("products", products);	
		
		GetAllCategories cate = new GetAllCategories();
		List<CategoriesBean> categories = cate.getCategory();
		request.setAttribute("categories", categories);
		request.getRequestDispatcher("/admin/layout/NotOnShelf.jsp").forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
