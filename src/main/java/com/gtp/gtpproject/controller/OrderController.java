package com.gtp.gtpproject.controller;

import com.example.booknest.dao.OrderDAO;
import com.example.booknest.model.Order;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class OrderController {
    private OrderDAO orderDAO;
    private Gson gson;

    public OrderController() {
        this.orderDAO = new OrderDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        post("/api/orders", (req, res) -> {
            res.type("application/json");
            try {
                Order order = gson.fromJson(req.body(), Order.class);
                boolean success = orderDAO.createOrder(order);

                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Order created successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to create order");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid order data: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        get("/api/orders/customer/:customerId", (req, res) -> {
            res.type("application/json");
            int customerId = Integer.parseInt(req.params(":customerId"));
            List<Order> orders = orderDAO.getOrdersByCustomer(customerId);
            return gson.toJson(orders);
        });

        get("/api/orders", (req, res) -> {
            res.type("application/json");
            List<Order> orders = orderDAO.getAllOrders();
            return gson.toJson(orders);
        });

        put("/api/orders/:id/status", (req, res) -> {
            res.type("application/json");
            try {
                int orderId = Integer.parseInt(req.params(":id"));
                Map<String, String> data = gson.fromJson(req.body(), Map.class);
                String status = data.get("status");

                boolean success = orderDAO.updateOrderStatus(orderId, status);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Order status updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to update order status");
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