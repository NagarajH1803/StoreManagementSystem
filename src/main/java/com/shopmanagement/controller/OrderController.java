package com.shopmanagement.controller;

import com.shopmanagement.model.Order;
import com.shopmanagement.model.OrderItem;
import com.shopmanagement.model.User;
import com.shopmanagement.service.OrderService;
import com.shopmanagement.serviceimpl.OrderServiceImpl;
import com.shopmanagement.util.PDFGenerator;
import com.shopmanagement.util.QRCodeGenerator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/orders", "/orders/place", "/orders/status", "/orders/pdf", "/orders/qr"})
public class OrderController extends HttpServlet {

    private final OrderService orderService = new OrderServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/orders/pdf".equals(path)) {
            handlePDFDownload(request, response);
            return;
        }

        if ("/orders/qr".equals(path)) {
            handleQRCode(request, response);
            return;
        }

        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        List<Order> orders;

        if ("MANAGER".equals(role)) {
            orders = orderService.findAll();
        } else {
            int userId = (int) session.getAttribute("userId");
            orders = orderService.findByUserId(userId);
        }

        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/WEB-INF/views/shopkeeper/orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/orders/place".equals(path)) {
            handlePlaceOrder(request, response);
        } else if ("/orders/status".equals(path)) {
            handleStatusUpdate(request, response);
        }
    }

    @SuppressWarnings("unchecked")
    private void handlePlaceOrder(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        String customerName = request.getParameter("customerName");
        String customerPhone = request.getParameter("customerPhone");

        List<OrderItem> cartItems = (List<OrderItem>) session.getAttribute("cart");
        if (cartItems == null || cartItems.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        double total = 0;
        for (OrderItem item : cartItems) {
            total += item.getSubtotal();
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setCustomerName(customerName);
        order.setCustomerPhone(customerPhone);
        order.setTotalAmount(total);
        order.setStatus("COMPLETED");
        order.setItems(cartItems);

        try {
            int orderId = orderService.placeOrder(order);
            session.removeAttribute("cart");
            session.removeAttribute("cartCount");
            response.sendRedirect(request.getContextPath() + "/orders?success=Order+%23" + orderId + "+placed+successfully");
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/shopkeeper/cart.jsp").forward(request, response);
        }
    }

    private void handleStatusUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        String status = request.getParameter("status");
        orderService.updateStatus(orderId, status);
        response.sendRedirect(request.getContextPath() + "/orders");
    }

    private void handlePDFDownload(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int orderId = Integer.parseInt(request.getParameter("id"));
        Order order = orderService.findById(orderId);

        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Invoice_" + orderId + ".pdf");

        try {
            PDFGenerator.generateBill(order, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate PDF");
        }
    }

    private void handleQRCode(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int orderId = Integer.parseInt(request.getParameter("id"));
        Order order = orderService.findById(orderId);

        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        try {
            QRCodeGenerator.generatePaymentQR(orderId, order.getTotalAmount(), response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate QR code");
        }
    }
}
