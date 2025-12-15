package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.google.gson.Gson;

import bean.Admin;
import bean.Admin;
import dao.AdminDao;


@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	AdminDao adminDao = new AdminDao(); 
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		String action = request.getParameter("action");
		System.out.println(action);
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		//取得存在session的權限
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("role") == null) {
		    response.setStatus(403);
		    out.write("尚未登入");
		    return;
		}

		String loginRole = (String) session.getAttribute("role");
		boolean isSuperAdmin = "super_admin".equals(loginRole);
		
		try {
			
			switch(action) {
			//查詢全部
			case "list":
				String status = request.getParameter("status"); // 前端下拉選單傳來 all, active, disabled
			    List<Admin> admins;

			    // 根據 status 呼叫不同 DAO 方法
			    if (status == null || status.equals("all")) {
			        admins = adminDao.queryAllAdmin();
			    } else if (status.equals("active")) {
			        admins = adminDao.queryActiveAdmins();
			    } else if (status.equals("disabled")) {
			        admins = adminDao.queryDisabledAdmins();
			    } else {
			        // 防呆：傳了未知 status
			        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			        out.print("{\"error\":\"未知的 status\"}");
			        return;
			    }

			    // 回傳 JSON
			    out.print(gson.toJson(admins));
			    break;
			    
				//依id查詢
			case "queryById":
				int id = Integer.parseInt(request.getParameter("adminId"));
				Admin admin = adminDao.queryAdminById(id);
				out.print(gson.toJson(admin));
				System.out.println("查詢資料"+id);
				break;
				//依姓名模糊查詢
			case "queryLikeName":
				String name = request.getParameter("name");
				List<Admin> result = adminDao.queryAdminsByName(name);
				out.print(gson.toJson(result));
				break;
				//停用啟用
			case "toggleStatus":
				if (!isSuperAdmin) {
		            response.setStatus(403);
		            out.write("權限不足：只有超級管理員可以停用");
		            return;
		        }
                int adminId = Integer.parseInt(request.getParameter("adminId"));
                boolean success = adminDao.toggleStatusAdmin(adminId);
                out.print(gson.toJson(success));
                break;
			case "create": 
				if (!isSuperAdmin) {
		            response.setStatus(403);
		            out.write("權限不足：只有超級管理員可以新增");
		            return;
		        }
			    String newName = request.getParameter("name");
			    String email = request.getParameter("email");
			    String password = request.getParameter("password");
			    String phone = request.getParameter("phone");
			    String role = request.getParameter("role");
			    System.out.println(request.getParameter("name"));
			    Admin newAdmin = new Admin(newName, email, password, phone, role); // 自動帶時間
			    Admin create = adminDao.createAdmin(newAdmin);
			    out.print(gson.toJson(create));
			    break;
			case "update":
				if (!isSuperAdmin) {
		            response.setStatus(403);
		            out.write("權限不足：只有超級管理員可以修改");
		            return;
		        }
			    try {
			        int updateId = Integer.parseInt(request.getParameter("adminId"));
			        String updateName = request.getParameter("name");
			        String updateEmail = request.getParameter("email");
			        String updatePhone = request.getParameter("phone");
			        String updateRole = request.getParameter("role");
			        String updateStatus = request.getParameter("status");
			        System.out.println("action=" + request.getParameter("action"));
			        System.out.println("adminId=" + request.getParameter("adminId"));
			        Admin updateAdmin = new Admin(updateId, updateEmail, updateName, updatePhone, updateRole,updateStatus);
			        System.out.println(updateName);
			        System.out.println(updateEmail);
			        System.out.println(updatePhone);
			        System.out.println(updateRole);
			        Admin updated = adminDao.updateAdmin(updateAdmin); 
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
