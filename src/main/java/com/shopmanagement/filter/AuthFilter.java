package com.shopmanagement.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/dashboard", "/products/*", "/orders/*", "/users/*", "/cart/*", "/shop", "/profile/*", "/categories/*", "/api/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        String path = request.getServletPath();

        // Allow public access to login, logout, index, and static resources
        if (path.equals("/login") || path.equals("/logout") ||
            path.startsWith("/assets/") || path.equals("/")) {
            chain.doFilter(request, response);
            return;
        }

        // Security headers
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");

        // Manager-only pages
        if ((path.equals("/dashboard") || path.startsWith("/users") || path.startsWith("/categories")) && !"MANAGER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        // Shopkeeper-only pages
        if ((path.startsWith("/cart") || path.equals("/shop")) && !"SHOPKEEPER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
