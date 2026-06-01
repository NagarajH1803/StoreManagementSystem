package com.shopmanagement.dao;

import com.shopmanagement.model.Order;
import java.util.List;
import java.util.Map;

public interface OrderDAO {
    List<Order> findAll();
    Order findById(int id);
    List<Order> findByUserId(int userId);
    int save(Order order);
    boolean updateStatus(int orderId, String status);
    double getTotalSales();
    int getTotalOrders();
    List<Map<String, Object>> getMonthlySales();
    List<Map<String, Object>> getTopProducts(int limit);
    List<Map<String, Object>> getDailySales(String startDate, String endDate);
}
