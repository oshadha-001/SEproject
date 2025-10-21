package com.example.booknest.controller;

import com.example.booknest.dao.SystemLogDAO;
import com.example.booknest.model.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class SystemLogController {
    private SystemLogDAO systemLogDAO;
    private Gson gson;

    public SystemLogController() {
        this.systemLogDAO = new SystemLogDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // Get system logs
        get("/api/logs", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || !"ADMIN".equals(user.getRole())) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                String action = req.queryParams("action");
                String tableName = req.queryParams("table");
                String startDate = req.queryParams("startDate");
                String endDate = req.queryParams("endDate");
                int limit = Integer.parseInt(req.queryParams("limit") != null ? req.queryParams("limit") : "100");

                List<Map<String, Object>> logs = systemLogDAO.getSystemLogs(action, tableName, startDate, endDate, limit);
                return gson.toJson(logs);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Get log statistics
        get("/api/logs/statistics", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || !"ADMIN".equals(user.getRole())) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                Map<String, Object> statistics = systemLogDAO.getLogStatistics();
                return gson.toJson(statistics);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Get user activity logs
        get("/api/logs/user/:userId", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int requestedUserId = Integer.parseInt(req.params(":userId"));
                if (!"ADMIN".equals(user.getRole()) && user.getUserId() != requestedUserId) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                List<Map<String, Object>> logs = systemLogDAO.getUserActivityLogs(requestedUserId);
                return gson.toJson(logs);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Clear old logs
        delete("/api/logs/cleanup", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || !"ADMIN".equals(user.getRole())) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int daysToKeep = Integer.parseInt(req.queryParams("days") != null ? req.queryParams("days") : "30");
                boolean success = systemLogDAO.cleanupOldLogs(daysToKeep);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Old logs cleaned up successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to clean up old logs");
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
