package com.example.booknest.controller;

import com.example.booknest.dao.OrderDAO;
import com.example.booknest.model.Order;
import com.example.booknest.model.User;
import com.example.booknest.service.OrderService;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class OrderController {
    private OrderDAO orderDAO;
    private OrderService orderService;
    private Gson gson;

    public OrderController() {
        this.orderDAO = new OrderDAO();
        this.orderService = new OrderService();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {

        // ✅ NEW ROUTE: Checkout from cart
        post("/api/checkout", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(401);
                    return gson.toJson(Map.of(
                            "status", "error",
                            "message", "Please login first"
                    ));
                }

                Map<String, String> data = gson.fromJson(req.body(), Map.class);
                String shippingAddress = data.get("shippingAddress");
                String paymentMethod = data.get("paymentMethod");

                if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
                    res.status(400);
                    return gson.toJson(Map.of(
                            "status", "error",
                            "message", "Shipping address is required"
                    ));
                }

                Map<String, Object> response = orderService.processCheckout(
                        user.getUserId(), shippingAddress, paymentMethod
                );
                return gson.toJson(response);

            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Checkout failed: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        // ✅ Existing route: Create new order
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

        // ✅ Existing route: Get orders by customer ID
        get("/api/orders/customer/:customerId", (req, res) -> {
            res.type("application/json");
            int customerId = Integer.parseInt(req.params(":customerId"));
            List<Order> orders = orderDAO.getOrdersByCustomer(customerId);
            return gson.toJson(orders);
        });

        // ✅ Existing route: Get all orders
        get("/api/orders", (req, res) -> {
            res.type("application/json");
            List<Order> orders = orderDAO.getAllOrders();
            return gson.toJson(orders);
        });

        // ✅ Existing route: Update order status
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
