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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pet.dao.member.MemberDao;
import com.pet.model.member.Member;


@WebServlet("/MemberServlet")
@MultipartConfig
public class MemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MemberDao memberDao = new MemberDao(); 
    
	private void handlePictureUpload(HttpServletRequest request, Member member) throws IOException, ServletException {
	    Part filePart = request.getPart("picture"); // 前端 input name="picture"
	    if (filePart != null && filePart.getSize() > 0) {
	        // 取得檔名
	        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

	        // 指定存放的資料夾
	        String uploadPath = "C:\\memberImages\\";
	        File uploadDir = new File(uploadPath);
	        if (!uploadDir.exists()) uploadDir.mkdirs(); // 如果資料夾不存在就創建

	        // 寫入
	        filePart.write(uploadPath + File.separator + fileName);

	        // 生成存入 DB 的 URL
	        String fileUrl = request.getContextPath() + "/memberImages/" + fileName;
	        member.setPicture(fileUrl);
	    }
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		String action = request.getParameter("action");
		System.out.println(action);
		PrintWriter out = response.getWriter();
		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd").create();
		
		try { 
			
			switch(action) {
			//查詢全部
			case "list":
			    String status = request.getParameter("status"); // 前端下拉選單傳來 all, active, disabled
			    List<Member> members;

			    // 根據 status 呼叫不同 DAO 方法
			    if (status == null || status.equals("all")) {
			        members = memberDao.queryAllMembers();
			    } else if (status.equals("active")) {
			        members = memberDao.queryActiveMembers();
			    } else if (status.equals("disabled")) {
			        members = memberDao.queryDisabledMembers();
			    } else {
			        // 防呆：傳了未知 status
			        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			        out.print("{\"error\":\"未知的 status\"}");
			        return;
			    }

			    // 回傳 JSON
			    out.print(gson.toJson(members));
			    break;
			    
				//依id查詢
			case "queryById":
				int id = Integer.parseInt(request.getParameter("memberId"));
				Member member = memberDao.queryMemberById(id);
				out.print(gson.toJson(member));
				break;
				
				//依姓名模糊查詢
			case "queryLikeName":
				String name = request.getParameter("name");
				List<Member> result = memberDao.queryMembersByName(name);
				out.print(gson.toJson(result));
				break;
				
				//停用啟用
			case "toggleStatus":
                int memberId = Integer.parseInt(request.getParameter("memberId"));
                boolean success = memberDao.toggleStatusMember(memberId);
                out.print(gson.toJson(success));
                break;
                
                //新增
			case "create": 
				String email = request.getParameter("email");
				String password = request.getParameter("password");
			    String newName = request.getParameter("name");
			    String gender = request.getParameter("gender");
			    String birthday = request.getParameter("birthday");
			    Date birthdayDate = null;
			    if (birthday != null && !birthday.isEmpty()) {
			        try {
			            birthdayDate = new SimpleDateFormat("yyyy/MM/dd").parse(birthday);
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			    }
			    String phone = request.getParameter("phone");
			    String address = request.getParameter("address");

			    Member newMember = new Member(email, password, newName, gender, birthdayDate, phone, address, null);
			    handlePictureUpload(request, newMember);
			    Member create = memberDao.createMember(newMember);
			    out.print(gson.toJson(create));

			    break;
			    
			    //修改
			case "update":
			    try {
			        int updateId = Integer.parseInt(request.getParameter("memberId"));
			        String updateEmail = request.getParameter("email");
			        String updateName = request.getParameter("name");
			        String updateGender = request.getParameter("gender");
			        String updateBirthday = request.getParameter("birthday");
				    Date updateBirthdayDate = null;
				    if (updateBirthday != null && !updateBirthday.isEmpty()) {
				        try {
				        	updateBirthdayDate = new SimpleDateFormat("yyyy/MM/dd").parse(updateBirthday);
				        } catch (Exception e) {
				            e.printStackTrace();
				        }
				    }
			        String updatePhone = request.getParameter("phone");
			        String updateAddress = request.getParameter("address");
			        Member updateMember = new Member(updateId,updateEmail,updateName,updateGender,updateBirthdayDate,updatePhone,updateAddress,null);
			        handlePictureUpload(request, updateMember);
			        Member updated = memberDao.updateMember(updateMember); 
			        out.print(gson.toJson(updated));
			        System.out.println("回傳: " + gson.toJson(updated));
			    } catch(Exception e){
			        e.printStackTrace();
			        response.getWriter().println("伺服器錯誤！");
			    }
			    break;
			default:
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.print("{\"error\":\"未知的 action\"}");
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
