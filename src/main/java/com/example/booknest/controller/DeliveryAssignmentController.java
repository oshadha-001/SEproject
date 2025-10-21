package com.example.booknest.controller;

import com.example.booknest.dao.DeliveryAssignmentDAO;
import com.example.booknest.model.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class DeliveryAssignmentController {
    private DeliveryAssignmentDAO deliveryAssignmentDAO;
    private Gson gson;

    public DeliveryAssignmentController() {
        this.deliveryAssignmentDAO = new DeliveryAssignmentDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // Get delivery assignments for staff
        get("/api/delivery/assignments", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(401);
                    return gson.toJson(Map.of("status", "error", "message", "Please login first"));
                }

                List<Map<String, Object>> assignments;
                if ("DELIVERY_STAFF".equals(user.getRole())) {
                    assignments = deliveryAssignmentDAO.getAssignmentsByStaff(user.getUserId());
                } else if ("ADMIN".equals(user.getRole()) || "INVENTORY_MANAGER".equals(user.getRole())) {
                    assignments = deliveryAssignmentDAO.getAllAssignments();
                } else {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }
                return gson.toJson(assignments);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Assign delivery to staff
        post("/api/delivery/assign", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                boolean success = deliveryAssignmentDAO.assignDelivery(data);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Delivery assigned successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to assign delivery");
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

        // Update delivery status
        put("/api/delivery/:id/status", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(401);
                    return gson.toJson(Map.of("status", "error", "message", "Please login first"));
                }

                int assignmentId = Integer.parseInt(req.params(":id"));
                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                String status = (String) data.get("status");
                String notes = (String) data.get("notes");

                boolean success = deliveryAssignmentDAO.updateDeliveryStatus(assignmentId, status, notes, user.getUserId());
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Delivery status updated successfully");
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

        // Complete delivery
        put("/api/delivery/:id/complete", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || !"DELIVERY_STAFF".equals(user.getRole())) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int assignmentId = Integer.parseInt(req.params(":id"));
                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                String deliveryNotes = (String) data.get("deliveryNotes");

                boolean success = deliveryAssignmentDAO.completeDelivery(assignmentId, deliveryNotes, user.getUserId());
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Delivery completed successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to complete delivery");
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

        // Get delivery analytics
        get("/api/delivery/analytics", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                Map<String, Object> analytics = deliveryAssignmentDAO.getDeliveryAnalytics();
                return gson.toJson(analytics);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });
    }
}
