package com.example.booknest.controller;

import com.example.booknest.dao.OrderDAO;
import com.example.booknest.model.Order;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class DeliveryController {
    private OrderDAO orderDAO;
    private Gson gson;

    public DeliveryController() {
        this.orderDAO = new OrderDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // Get delivery orders
        get("/api/delivery/orders", (req, res) -> {
            res.type("application/json");
            // Get orders that are pending or picked up for delivery
            List<Order> orders = orderDAO.getAllOrders();
            // Filter orders for delivery (PENDING, PICKED_UP status)
            List<Order> deliveryOrders = orders.stream()
                    .filter(order -> "PENDING".equals(order.getStatus()) || 
                                   "PICKED_UP".equals(order.getStatus()))
                    .toList();
            return gson.toJson(deliveryOrders);
        });

        // Get delivery statistics
        get("/api/delivery/stats", (req, res) -> {
            res.type("application/json");
            List<Order> orders = orderDAO.getAllOrders();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("pendingDeliveries", orders.stream()
                    .filter(order -> "PENDING".equals(order.getStatus()))
                    .count());
            stats.put("completedToday", orders.stream()
                    .filter(order -> "DELIVERED".equals(order.getStatus()))
                    .count()); // This should be filtered by today's date
            stats.put("totalDeliveries", orders.stream()
                    .filter(order -> "DELIVERED".equals(order.getStatus()))
                    .count());
            stats.put("deliveryZone", "All Zones"); // This should come from user profile
            
            return gson.toJson(stats);
        });

        // Update delivery status
        put("/api/delivery/orders/:id/status", (req, res) -> {
            res.type("application/json");
            try {
                int orderId = Integer.parseInt(req.params(":id"));
                Map<String, String> data = gson.fromJson(req.body(), Map.class);
                String status = data.get("status");
                String remarks = data.get("remarks");

                boolean success = orderDAO.updateOrderStatus(orderId, status);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Delivery status updated successfully");
                    // Here you would also save delivery remarks
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to update delivery status");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid request: " + e.getMessage());
                return gson.toJson(response);
            }
        });
    }
}