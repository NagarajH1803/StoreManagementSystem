package com.shopmanagement.controller;

import com.shopmanagement.model.User;
import com.shopmanagement.service.UserService;
import com.shopmanagement.serviceimpl.UserServiceImpl;
import com.shopmanagement.util.PasswordUtil;
import com.shopmanagement.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"/profile", "/profile/password"})
public class ProfileController extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validation
        if (!ValidationUtil.isNotBlank(currentPassword) || !ValidationUtil.isNotBlank(newPassword)) {
            request.setAttribute("errorMessage", "All password fields are required.");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            return;
        }

        if (newPassword.length() < 4) {
            request.setAttribute("errorMessage", "New password must be at least 4 characters.");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "New passwords do not match.");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            return;
        }

        // Verify current password
        User user = userService.findById(userId);
        if (user == null || !PasswordUtil.verifyPassword(currentPassword, user.getPassword())) {
            request.setAttribute("errorMessage", "Current password is incorrect.");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            return;
        }

        // Update password with BCrypt
        String newHash = PasswordUtil.hashPassword(newPassword);
        userService.updatePassword(userId, newHash);

        request.setAttribute("successMessage", "Password changed successfully!");
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
}
