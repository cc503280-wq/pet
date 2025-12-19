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
import com.pet.dao.member.MemberPetDao;
import com.pet.model.member.Member;
import com.pet.model.member.MemberPet;
import com.pet.utils.HibernateUtil;


@WebServlet("/MemberPetServlet")
@MultipartConfig
public class MemberPetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		MemberPetDao memberPetDao = new MemberPetDao(session);
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		String action = request.getParameter("action");
		System.out.println(action);
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		
		try { 
			
			switch(action) {
			//查詢全部
			case "list":

			    List<MemberPet> memberPets = memberPetDao.queryAllMemberPets();
                out.print(gson.toJson(memberPets));
                break;

				//依petId查詢
			case "queryByPetId":
                int petId = Integer.parseInt(request.getParameter("petId"));
                MemberPet pet = memberPetDao.queryMemberPetByPetId(petId);
                out.print(gson.toJson(pet));
                break;
				
				//依 memberId 查詢
			case "queryByMemberId":
                int memberId = Integer.parseInt(request.getParameter("memberId"));
                System.out.println(request.getParameter("memberId"));
                List<MemberPet> petsOfMember = memberPetDao.queryMemberPetByMemberId(memberId);
                out.print(gson.toJson(petsOfMember));
                break;
				
				//條件查詢
			case "search":
                String type = request.getParameter("petType");
                String age = request.getParameter("petAge");
                String size = request.getParameter("petSize");

                List<MemberPet> filteredPets = memberPetDao.queryPetsByConditions(type, age, size);
                out.print(gson.toJson(filteredPets));
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
