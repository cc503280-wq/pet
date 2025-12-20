package com.pet.controller.member;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.Session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pet.dao.member.FavoritesDao;
import com.pet.model.member.Favorites;
import com.pet.model.member.Member;
import com.pet.utils.HibernateUtil;


@WebServlet("/FavoritesServlet")
@MultipartConfig
public class FavoritesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		FavoritesDao favoritesDao = new FavoritesDao(session);
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		String action = request.getParameter("action");
		System.out.println(action);
		Gson gson = new Gson();
		PrintWriter out = response.getWriter();
		
		try { 
			
			switch(action) {
			//查詢全部
			case "list":

			    List<Favorites> favorites = favoritesDao.queryAllFavorites();
                out.print(gson.toJson(favorites));
                break;

				//依productId查詢
			case "queryByProductId":
                int productId = Integer.parseInt(request.getParameter("productId"));
                List<Favorites> favorites1 = favoritesDao.queryFavoritesByProductId(productId);
                out.print(gson.toJson(favorites1));
                break;
				
				//依 memberId 查詢
			case "queryByMemberId":
                int memberId = Integer.parseInt(request.getParameter("memberId"));
                List<Favorites> favorite2 = favoritesDao.queryFavoritesByMemberId(memberId);
                out.print(gson.toJson(favorite2));
                break;
				
			default:
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.print("{\"error\":\"未知的 action\"}");
				break;
			}
			
		} catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"伺服器錯誤\"}");
        }
		out.flush();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
