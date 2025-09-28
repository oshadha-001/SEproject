package com.gtp.gtpproject.controller;

import com.example.booknest.dao.OrderDAO;
import com.google.gson.Gson;

import java.util.HashMap;
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
        get("/api/delivery/orders", (req, res) -> {
            res.type("application/json");
            // Get orders assigned to delivery staff (implementation depends on your logic)
            return gson.toJson(new HashMap<>());
        });

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