package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import dao.GetAllCategories;
import dao.GetFuzzySearch;
import bean.CategoriesBean;
import bean.ProductBean;



@WebServlet("/FuzzyNotOnShelf")
public class FuzzyNotOnShelf extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String keyword = request.getParameter("keyword");
		GetFuzzySearch fuzzySearch = new GetFuzzySearch();
		List<ProductBean> products = fuzzySearch.getNotOnShelf(keyword);
		request.setAttribute("products", products);	
		GetAllCategories cate = new GetAllCategories();
		List<CategoriesBean> categories = cate.getCategory();
		request.setAttribute("categories", categories);
		request.getRequestDispatcher("/admin/layout/FuzzyNotOnShelf.jsp").forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
