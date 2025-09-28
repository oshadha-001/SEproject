package com.gtp.gtpproject.service;

import com.example.booknest.dao.BookDAO;
import com.example.booknest.dao.OrderDAO;
import com.example.booknest.model.Order;
import com.example.booknest.model.OrderItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    private OrderDAO orderDAO;
    private BookDAO bookDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.bookDAO = new BookDAO();
    }

    public Map<String, Object> createOrder(Order order) {
        Map<String, Object> response = new HashMap<>();

        // Validate order
        if (order.getCustomerId() <= 0) {
            response.put("status", "error");
            response.put("message", "Invalid customer ID");
            return response;
        }

        if (order.getTotalAmount() <= 0) {
            response.put("status", "error");
            response.put("message", "Invalid order amount");
            return response;
        }

        if (order.getShippingAddress() == null || order.getShippingAddress().trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Shipping address is required");
            return response;
        }

        // Check stock availability
        for (OrderItem item : order.getOrderItems()) {
            var book = bookDAO.getBookById(item.getBookId());
            if (book == null) {
                response.put("status", "error");
                response.put("message", "Book not found: ID " + item.getBookId());
                return response;
            }

            if (book.getStockQuantity() < item.getQuantity()) {
                response.put("status", "error");
                response.put("message", "Insufficient stock for: " + book.getTitle());
                return response;
            }
        }

        // Create order
        boolean success = orderDAO.createOrder(order);
        if (success) {
            // Update stock quantities
            for (OrderItem item : order.getOrderItems()) {
                var book = bookDAO.getBookById(item.getBookId());
                int newStock = book.getStockQuantity() - item.getQuantity();
                bookDAO.updateBookStock(item.getBookId(), newStock);
            }

            response.put("status", "success");
            response.put("message", "Order created successfully");
            response.put("orderId", order.getOrderId());
        } else {
            response.put("status", "error");
            response.put("message", "Failed to create order");
        }

        return response;
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        return orderDAO.getOrdersByCustomer(customerId);
    }

    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    public Map<String, Object> updateOrderStatus(int orderId, String status) {
        Map<String, Object> response = new HashMap<>();

        // Validate status
        List<String> validStatuses = List.of("PENDING", "PROCESSING", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED");
        if (!validStatuses.contains(status.toUpperCase())) {
            response.put("status", "error");
            response.put("message", "Invalid order status");
            return response;
        }

        boolean success = orderDAO.updateOrderStatus(orderId, status);
        if (success) {
            response.put("status", "success");
            response.put("message", "Order status updated successfully");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to update order status");
        }

        return response;
    }

    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<Order> allOrders = getAllOrders();

        int totalOrders = allOrders.size();
        int pendingOrders = (int) allOrders.stream()
                .filter(order -> "PENDING".equals(order.getStatus()))
                .count();
        int deliveredOrders = (int) allOrders.stream()
                .filter(order -> "DELIVERED".equals(order.getStatus()))
                .count();
        double totalRevenue = allOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("deliveredOrders", deliveredOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("averageOrderValue", totalOrders > 0 ? totalRevenue / totalOrders : 0);

        return stats;
    }
}