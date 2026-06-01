package com.shopmanagement.controller;

import com.shopmanagement.model.Role;
import com.shopmanagement.model.User;
import com.shopmanagement.service.UserService;
import com.shopmanagement.serviceimpl.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/users", "/users/add", "/users/update", "/users/delete"})
public class UserController extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/users/delete".equals(path)) {
            handleDelete(request, response);
            return;
        }

        List<User> users = userService.findAll();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/WEB-INF/views/manager/manage-users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/users/add".equals(path)) {
            handleAdd(request, response);
        } else if ("/users/update".equals(path)) {
            handleUpdate(request, response);
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        User user = new User();
        user.setUsername(request.getParameter("username"));
        user.setFullName(request.getParameter("fullName"));
        user.setEmail(request.getParameter("email"));
        user.setRole(Role.valueOf(request.getParameter("role")));

        String password = request.getParameter("password");

        boolean success = userService.register(user, password);
        if (!success) {
            request.setAttribute("errorMessage", "Username already exists!");
            List<User> users = userService.findAll();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/manager/manage-users.jsp").forward(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/users?success=User+added+successfully");
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User user = new User();
        user.setId(Integer.parseInt(request.getParameter("id")));
        user.setUsername(request.getParameter("username"));
        user.setFullName(request.getParameter("fullName"));
        user.setEmail(request.getParameter("email"));
        user.setRole(Role.valueOf(request.getParameter("role")));

        userService.update(user);
        response.sendRedirect(request.getContextPath() + "/users?success=User+updated+successfully");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            userService.delete(Integer.parseInt(idParam));
        }
        response.sendRedirect(request.getContextPath() + "/users?success=User+deleted+successfully");
    }
}
