package com.shopmanagement.controller;

import com.shopmanagement.model.Category;
import com.shopmanagement.dao.CategoryDAO;
import com.shopmanagement.daoimpl.CategoryDAOImpl;
import com.shopmanagement.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/categories", "/categories/add", "/categories/update", "/categories/delete"})
public class CategoryController extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/categories/delete".equals(path)) {
            int id = ValidationUtil.parseIntSafe(request.getParameter("id"), 0);
            if (id > 0) {
                categoryDAO.delete(id);
            }
            response.sendRedirect(request.getContextPath() + "/categories?success=Category+deleted");
            return;
        }

        List<Category> categories = categoryDAO.findAll();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/WEB-INF/views/manager/manage-categories.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/categories/add".equals(path)) {
            String name = ValidationUtil.sanitize(request.getParameter("name"));
            if (ValidationUtil.isNotBlank(name)) {
                Category category = new Category();
                category.setName(name);
                categoryDAO.save(category);
            }
            response.sendRedirect(request.getContextPath() + "/categories?success=Category+added");
        } else if ("/categories/update".equals(path)) {
            int id = ValidationUtil.parseIntSafe(request.getParameter("id"), 0);
            String name = ValidationUtil.sanitize(request.getParameter("name"));
            if (id > 0 && ValidationUtil.isNotBlank(name)) {
                Category category = new Category();
                category.setId(id);
                category.setName(name);
                categoryDAO.update(category);
            }
            response.sendRedirect(request.getContextPath() + "/categories?success=Category+updated");
        }
    }
}
