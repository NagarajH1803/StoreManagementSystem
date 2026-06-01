package com.shopmanagement.daoimpl;

import com.shopmanagement.dao.OrderDAO;
import com.shopmanagement.model.Order;
import com.shopmanagement.model.OrderItem;
import com.shopmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS shopkeeper_name FROM orders o JOIN users u ON o.user_id = u.id ORDER BY o.order_date DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Order order = mapOrder(rs);
                order.setShopkeeperName(rs.getString("shopkeeper_name"));
                order.setItems(findItemsByOrderId(conn, order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return orders;
    }

    @Override
    public Order findById(int id) {
        String sql = "SELECT o.*, u.full_name AS shopkeeper_name FROM orders o JOIN users u ON o.user_id = u.id WHERE o.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Order order = mapOrder(rs);
                order.setShopkeeperName(rs.getString("shopkeeper_name"));
                order.setItems(findItemsByOrderId(conn, order.getId()));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return null;
    }

    @Override
    public List<Order> findByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS shopkeeper_name FROM orders o JOIN users u ON o.user_id = u.id WHERE o.user_id = ? ORDER BY o.order_date DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Order order = mapOrder(rs);
                order.setShopkeeperName(rs.getString("shopkeeper_name"));
                order.setItems(findItemsByOrderId(conn, order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return orders;
    }

    @Override
    public int save(Order order) {
        String orderSql = "INSERT INTO orders (user_id, customer_name, customer_phone, total_amount, status) VALUES (?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement orderPs = null;
        PreparedStatement itemPs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            orderPs = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderPs.setInt(1, order.getUserId());
            orderPs.setString(2, order.getCustomerName());
            orderPs.setString(3, order.getCustomerPhone());
            orderPs.setDouble(4, order.getTotalAmount());
            orderPs.setString(5, order.getStatus() != null ? order.getStatus() : "COMPLETED");
            orderPs.executeUpdate();

            ResultSet keys = orderPs.getGeneratedKeys();
            int orderId = 0;
            if (keys.next()) {
                orderId = keys.getInt(1);
            }

            if (order.getItems() != null) {
                itemPs = conn.prepareStatement(itemSql);
                for (OrderItem item : order.getItems()) {
                    itemPs.setInt(1, orderId);
                    itemPs.setInt(2, item.getProductId());
                    itemPs.setInt(3, item.getQuantity());
                    itemPs.setDouble(4, item.getUnitPrice());
                    itemPs.addBatch();
                }
                itemPs.executeBatch();
            }

            conn.commit();
            return orderId;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
            DBConnection.close(itemPs, orderPs, conn);
        }
        return 0;
    }

    @Override
    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(ps, conn);
        }
        return false;
    }

    @Override
    public double getTotalSales() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total FROM orders WHERE status = 'COMPLETED'";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return 0;
    }

    @Override
    public int getTotalOrders() {
        String sql = "SELECT COUNT(*) AS total FROM orders";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return 0;
    }

    @Override
    public List<Map<String, Object>> getMonthlySales() {
        List<Map<String, Object>> data = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') AS month, SUM(total_amount) AS total " +
                     "FROM orders WHERE status='COMPLETED' GROUP BY month ORDER BY month DESC LIMIT 12";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("month", rs.getString("month"));
                row.put("total", rs.getDouble("total"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return data;
    }

    @Override
    public List<Map<String, Object>> getTopProducts(int limit) {
        List<Map<String, Object>> data = new ArrayList<>();
        String sql = "SELECT p.name, SUM(oi.quantity) AS total_qty, SUM(oi.quantity * oi.unit_price) AS total_revenue " +
                     "FROM order_items oi JOIN products p ON oi.product_id = p.id " +
                     "JOIN orders o ON oi.order_id = o.id WHERE o.status='COMPLETED' " +
                     "GROUP BY p.id, p.name ORDER BY total_qty DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("name", rs.getString("name"));
                row.put("totalQty", rs.getInt("total_qty"));
                row.put("totalRevenue", rs.getDouble("total_revenue"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return data;
    }

    @Override
    public List<Map<String, Object>> getDailySales(String startDate, String endDate) {
        List<Map<String, Object>> data = new ArrayList<>();
        String sql = "SELECT DATE(order_date) AS sale_date, COUNT(*) AS order_count, SUM(total_amount) AS total " +
                     "FROM orders WHERE status='COMPLETED' AND DATE(order_date) BETWEEN ? AND ? " +
                     "GROUP BY sale_date ORDER BY sale_date";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("date", rs.getString("sale_date"));
                row.put("orderCount", rs.getInt("order_count"));
                row.put("total", rs.getDouble("total"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return data;
    }

    private List<OrderItem> findItemsByOrderId(Connection conn, int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name AS product_name FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, orderId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            OrderItem item = new OrderItem();
            item.setId(rs.getInt("id"));
            item.setOrderId(rs.getInt("order_id"));
            item.setProductId(rs.getInt("product_id"));
            item.setProductName(rs.getString("product_name"));
            item.setQuantity(rs.getInt("quantity"));
            item.setUnitPrice(rs.getDouble("unit_price"));
            items.add(item);
        }
        DBConnection.close(rs, ps);
        return items;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setCustomerPhone(rs.getString("customer_phone"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setStatus(rs.getString("status"));
        return order;
    }
}
