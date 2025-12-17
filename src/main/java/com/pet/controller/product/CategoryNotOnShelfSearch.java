package com.pet.controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.pet.dao.product.GetAllCategories;
import com.pet.dao.product.GetAllProducts;
import com.pet.model.product.CategoriesBean;
import com.pet.model.product.ProductBean;

@WebServlet("/CategoryNotOnShelfSearch")
public class CategoryNotOnShelfSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("category_id");
		GetAllProducts category = new GetAllProducts();
		List<ProductBean> products = category.getCategoryNotOnShelfSearch(id);
		request.setAttribute("products", products);	
		
		GetAllCategories cate = new GetAllCategories();
		List<CategoriesBean> categories = cate.getCategory();
		request.setAttribute("categories", categories);
		request.setAttribute("selectedCategoryId", id);
		request.getRequestDispatcher("/admin/layout/CategoryNotOnShelf.jsp").forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
