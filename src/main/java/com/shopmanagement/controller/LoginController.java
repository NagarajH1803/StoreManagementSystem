package com.shopmanagement.controller;

import com.shopmanagement.model.User;
import com.shopmanagement.service.UserService;
import com.shopmanagement.serviceimpl.UserServiceImpl;
import com.shopmanagement.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/logout"})
public class LoginController extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Check if already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            redirectByRole(user, request, response);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userService.authenticate(username, password);

        if (user != null) {
            // Session fixation protection: invalidate old session, create new one
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);

            // Lazy BCrypt migration: re-hash password if still using SHA-256
            if (PasswordUtil.needsMigration(user.getPassword())) {
                String newHash = PasswordUtil.hashPassword(password);
                userService.updatePassword(user.getId(), newHash);
            }

            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("role", user.getRole().name());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            redirectByRole(user, request, response);
        } else {
            request.setAttribute("errorMessage", "Invalid username or password!");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }

    private void redirectByRole(User user, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        switch (user.getRole()) {
            case MANAGER:
                response.sendRedirect(request.getContextPath() + "/dashboard");
                break;
            case SHOPKEEPER:
                response.sendRedirect(request.getContextPath() + "/products");
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}
