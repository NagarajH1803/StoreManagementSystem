package com.shopmanagement.controller;

import com.shopmanagement.model.OrderItem;
import com.shopmanagement.model.Product;
import com.shopmanagement.service.ProductService;
import com.shopmanagement.serviceimpl.ProductServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@WebServlet(urlPatterns = {"/cart", "/cart/add", "/cart/remove", "/cart/update"})
public class CartController extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/shopkeeper/cart.jsp").forward(request, response);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        HttpSession session = request.getSession();
        List<OrderItem> cart = (List<OrderItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        switch (path) {
            case "/cart/add":
                handleAdd(request, cart);
                break;
            case "/cart/remove":
                handleRemove(request, cart);
                break;
            case "/cart/update":
                handleUpdate(request, cart);
                break;
        }

        session.setAttribute("cart", cart);
        session.setAttribute("cartCount", cart.stream().mapToInt(OrderItem::getQuantity).sum());

        String referer = request.getHeader("Referer");
        if (referer != null && !path.equals("/cart/update")) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void handleAdd(HttpServletRequest request, List<OrderItem> cart) {
        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = 1;
        String qtyParam = request.getParameter("quantity");
        if (qtyParam != null && !qtyParam.isEmpty()) {
            quantity = Integer.parseInt(qtyParam);
        }

        // Check if product already in cart
        for (OrderItem item : cart) {
            if (item.getProductId() == productId) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        // Add new item
        Product product = productService.findById(productId);
        if (product != null) {
            OrderItem item = new OrderItem(product.getId(), product.getName(), quantity, product.getPrice());
            cart.add(item);
        }
    }

    private void handleRemove(HttpServletRequest request, List<OrderItem> cart) {
        int productId = Integer.parseInt(request.getParameter("productId"));
        Iterator<OrderItem> iterator = cart.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getProductId() == productId) {
                iterator.remove();
                break;
            }
        }
    }

    private void handleUpdate(HttpServletRequest request, List<OrderItem> cart) {
        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        for (OrderItem item : cart) {
            if (item.getProductId() == productId) {
                if (quantity <= 0) {
                    cart.remove(item);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }
}
