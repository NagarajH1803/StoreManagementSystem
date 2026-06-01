package com.shopmanagement.serviceimpl;

import com.shopmanagement.dao.OrderDAO;
import com.shopmanagement.dao.ProductDAO;
import com.shopmanagement.daoimpl.OrderDAOImpl;
import com.shopmanagement.daoimpl.ProductDAOImpl;
import com.shopmanagement.model.Order;
import com.shopmanagement.model.OrderItem;
import com.shopmanagement.service.OrderService;

import java.util.List;
import java.util.Map;

public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final ProductDAO productDAO = new ProductDAOImpl();

    @Override
    public List<Order> findAll() {
        return orderDAO.findAll();
    }

    @Override
    public Order findById(int id) {
        return orderDAO.findById(id);
    }

    @Override
    public List<Order> findByUserId(int userId) {
        return orderDAO.findByUserId(userId);
    }

    @Override
    public int placeOrder(Order order) {
        // Deduct stock for each item
        for (OrderItem item : order.getItems()) {
            boolean stockUpdated = productDAO.updateStock(item.getProductId(), item.getQuantity());
            if (!stockUpdated) {
                throw new RuntimeException("Insufficient stock for product: " + item.getProductName());
            }
        }
        return orderDAO.save(order);
    }

    @Override
    public boolean updateStatus(int orderId, String status) {
        return orderDAO.updateStatus(orderId, status);
    }

    @Override
    public double getTotalSales() {
        return orderDAO.getTotalSales();
    }

    @Override
    public int getTotalOrders() {
        return orderDAO.getTotalOrders();
    }

    @Override
    public List<Map<String, Object>> getMonthlySales() {
        return orderDAO.getMonthlySales();
    }

    @Override
    public List<Map<String, Object>> getTopProducts(int limit) {
        return orderDAO.getTopProducts(limit);
    }

    @Override
    public List<Map<String, Object>> getDailySales(String startDate, String endDate) {
        return orderDAO.getDailySales(startDate, endDate);
    }
}
