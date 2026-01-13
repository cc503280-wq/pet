package com.pet.util;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        boolean loggedIn = (session != null) && (session.getAttribute("adminId") != null);

        if (loggedIn) {
            return true; // 已登入，放行
        }

        // 未登入，轉跳登入頁
        response.sendRedirect(request.getContextPath() + "/admin/layout/Login.html");
        return false;
    }

}
