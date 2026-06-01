package com.shopmanagement.controller;

import com.shopmanagement.dao.CategoryDAO;
import com.shopmanagement.daoimpl.CategoryDAOImpl;
import com.shopmanagement.model.Product;
import com.shopmanagement.service.OrderService;
import com.shopmanagement.service.ProductService;
import com.shopmanagement.serviceimpl.OrderServiceImpl;
import com.shopmanagement.serviceimpl.ProductServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {

    private final OrderService orderService = new OrderServiceImpl();
    private final ProductService productService = new ProductServiceImpl();
    private final CategoryDAO categoryDAO = new CategoryDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Sales stats
        double totalSales = orderService.getTotalSales();
        int totalOrders = orderService.getTotalOrders();
        List<Product> products = productService.findAll();
        int totalProducts = products.size();

        // Low stock products (stock < 10)
        long lowStockCount = products.stream().filter(p -> p.getStock() < 10).count();

        // Monthly sales for chart
        List<Map<String, Object>> monthlySales = orderService.getMonthlySales();

        // Top 5 products
        List<Map<String, Object>> topProducts = orderService.getTopProducts(5);

        request.setAttribute("totalSales", totalSales);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("lowStockCount", lowStockCount);
        request.setAttribute("monthlySales", monthlySales);
        request.setAttribute("topProducts", topProducts);
        request.setAttribute("categories", categoryDAO.findAll());

        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }
}
