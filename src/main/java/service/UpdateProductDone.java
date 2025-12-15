package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;

import dao.DoUpdateProduct;
import dao.InputImage;
import bean.ProductBean;

@WebServlet("/UpdateProductDone")
@MultipartConfig(location = "C:/images/")
public class UpdateProductDone extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Part part = request.getPart("photo");
		String filename = part.getSubmittedFileName();
		part.write(filename);
		String id = request.getParameter("id");
		String pname = request.getParameter("pname");
		String pdes = request.getParameter("pdes");
		String price = request.getParameter("price");
		String stock = request.getParameter("stock");
		String value =request.getParameter("category");
		String image = filename;
		String[] parts = value.split(",");
		String categoryid = parts[0];
		String categoryname = parts[1];
		String expiredate= request.getParameter("expiredate");
		String is_acitve = request.getParameter("is_acitve");	
		DoUpdateProduct update = new DoUpdateProduct();
		InputImage inputImage = new InputImage();
		ProductBean product= update.updateProduct(pname, pdes, price, stock
				, categoryid, image,categoryname, expiredate, is_acitve,id);
		inputImage.setimage(product);	
		request.setAttribute("product", product);
		request.setAttribute("product", product);
		request.getRequestDispatcher("/admin/layout/UpdateProductDone.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
