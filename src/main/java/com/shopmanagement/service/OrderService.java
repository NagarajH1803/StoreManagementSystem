package com.shopmanagement.service;

import com.shopmanagement.model.Order;
import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Order> findAll();
    Order findById(int id);
    List<Order> findByUserId(int userId);
    int placeOrder(Order order);
    boolean updateStatus(int orderId, String status);
    double getTotalSales();
    int getTotalOrders();
    List<Map<String, Object>> getMonthlySales();
    List<Map<String, Object>> getTopProducts(int limit);
    List<Map<String, Object>> getDailySales(String startDate, String endDate);
}
