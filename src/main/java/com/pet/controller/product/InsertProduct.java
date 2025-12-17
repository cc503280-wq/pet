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
//import java.nio.file.Files;

import com.pet.dao.product.DoInsertProduct;
import com.pet.dao.product.InputImage;
import com.pet.model.product.ProductBean;

@WebServlet("/InsertProduct")
@MultipartConfig(location="C:/images/")
public class InsertProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Part part = request.getPart("photo");
		String filename = part.getSubmittedFileName();	
		part.write(filename);
		
		String pname = request.getParameter("pname");
		String pdes = request.getParameter("pdes");
		String price = request.getParameter("price");
		String stock = request.getParameter("stock");
		String value =request.getParameter("category");
		String[] parts = value.split(",");
		String categoryid = parts[0];
		String categoryname = parts[1];
		String expiredate= request.getParameter("expiredate");
		String is_acitve = request.getParameter("is_acitve");		
		String imageurl = filename;
		InputImage inputImage = new InputImage();
		
		DoInsertProduct doInsertProduct = new DoInsertProduct();
		ProductBean product = doInsertProduct.setProduct(pname, pdes, price, stock, categoryid,imageurl, categoryname, expiredate, is_acitve);
		product.setImageUrl(imageurl);
		inputImage.setimage(product);		
		
		
		
		request.setAttribute("product", product);
		request.getRequestDispatcher("/admin/layout/InsertProductDone.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
