package com.pet.utils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class AdminAuthFilter extends HttpFilter implements Filter {
       
	public void destroy() {
	}
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // 判斷 session 是否存在 adminId
        boolean loggedIn = (session != null) && (session.getAttribute("adminId") != null);

        // 登入頁面 & 登出頁面不過濾
        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (path.startsWith("/AdminLoginServlet") || path.startsWith("/AdminLogoutServlet") || path.equals("/admin/layout/Login.html")) {
            chain.doFilter(request, response);
            return;
        }

        if (loggedIn) {
            chain.doFilter(request, response); // 已登入，放行
        } else {
            // 未登入，轉跳登入頁
            resp.sendRedirect(req.getContextPath() + "/admin/layout/Login.html");
        }
	}
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
