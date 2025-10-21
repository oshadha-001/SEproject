package com.example.booknest.controller;

import com.example.booknest.dao.PerformanceDAO;
import com.example.booknest.model.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class PerformanceController {
    private PerformanceDAO performanceDAO;
    private Gson gson;

    public PerformanceController() {
        this.performanceDAO = new PerformanceDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // Get performance records for a user
        get("/api/performance/user/:userId", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int userId = Integer.parseInt(req.params(":userId"));
                List<Map<String, Object>> records = performanceDAO.getPerformanceRecords(userId);
                return gson.toJson(records);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Add performance record
        post("/api/performance", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                boolean success = performanceDAO.addPerformanceRecord(data);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Performance record added successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to add performance record");
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

        // Update performance record
        put("/api/performance/:id", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int recordId = Integer.parseInt(req.params(":id"));
                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                boolean success = performanceDAO.updatePerformanceRecord(recordId, data);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Performance record updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to update performance record");
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

        // Get performance analytics
        get("/api/performance/analytics", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || !"ADMIN".equals(user.getRole())) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                Map<String, Object> analytics = performanceDAO.getPerformanceAnalytics();
                return gson.toJson(analytics);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Get top performers
        get("/api/performance/top-performers", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int limit = Integer.parseInt(req.queryParams("limit") != null ? req.queryParams("limit") : "10");
                List<Map<String, Object>> topPerformers = performanceDAO.getTopPerformers(limit);
                return gson.toJson(topPerformers);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });
    }
}
