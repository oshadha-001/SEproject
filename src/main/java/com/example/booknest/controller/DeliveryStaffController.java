package com.example.booknest.controller;

import com.example.booknest.dao.DeliveryStaffDAO;
import com.example.booknest.model.DeliveryStaff;
import com.example.booknest.model.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class DeliveryStaffController {
    private DeliveryStaffDAO deliveryStaffDAO;
    private Gson gson;

    public DeliveryStaffController() {
        this.deliveryStaffDAO = new DeliveryStaffDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // Get all delivery staff (Admin only)
        get("/api/delivery-staff", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"BOOKSTORE_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                List<Map<String, Object>> staffList = deliveryStaffDAO.getAllDeliveryStaff();
                return gson.toJson(staffList);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Failed to retrieve delivery staff: " + e.getMessage()));
            }
        });

        // Get delivery staff by ID
        get("/api/delivery-staff/:id", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int staffId = Integer.parseInt(req.params(":id"));
                Map<String, Object> staff = deliveryStaffDAO.getDeliveryStaffById(staffId);

                if (staff != null) {
                    return gson.toJson(staff);
                } else {
                    res.status(404);
                    return gson.toJson(Map.of("status", "error", "message", "Delivery staff not found"));
                }
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Get delivery staff by user ID
        get("/api/delivery-staff/user/:userId", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int userId = Integer.parseInt(req.params(":userId"));
                Map<String, Object> staff = deliveryStaffDAO.getDeliveryStaffByUserId(userId);

                if (staff != null) {
                    return gson.toJson(staff);
                } else {
                    res.status(404);
                    return gson.toJson(Map.of("status", "error", "message", "Delivery staff not found"));
                }
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Add new delivery staff (Admin only)
        post("/api/delivery-staff", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"BOOKSTORE_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                Map<String, Object> staffData = gson.fromJson(req.body(), Map.class);
                DeliveryStaff staff = new DeliveryStaff();
                staff.setUserId(((Number) staffData.get("userId")).intValue());
                staff.setVehicleType((String) staffData.get("vehicleType"));
                staff.setLicenseNumber((String) staffData.get("licenseNumber"));

                boolean success = deliveryStaffDAO.addDeliveryStaff(staff);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Delivery staff added successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to add delivery staff");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request data: " + e.getMessage()));
            }
        });

        // Update delivery staff (Admin or own profile)
        put("/api/delivery-staff/:id", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int staffId = Integer.parseInt(req.params(":id"));
                Map<String, Object> staffData = gson.fromJson(req.body(), Map.class);

                // Check if user has permission (Admin or updating own profile)
                Map<String, Object> existingStaff = deliveryStaffDAO.getDeliveryStaffById(staffId);
                if (existingStaff == null) {
                    res.status(404);
                    return gson.toJson(Map.of("status", "error", "message", "Delivery staff not found"));
                }

                int staffUserId = ((Number) existingStaff.get("userId")).intValue();
                if (!"ADMIN".equals(user.getRole()) && !"BOOKSTORE_MANAGER".equals(user.getRole()) && user.getUserId() != staffUserId) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                DeliveryStaff staff = new DeliveryStaff();
                staff.setStaffId(staffId);
                staff.setVehicleType((String) staffData.get("vehicleType"));
                staff.setLicenseNumber((String) staffData.get("licenseNumber"));

                boolean success = deliveryStaffDAO.updateDeliveryStaff(staff);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Delivery staff updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to update delivery staff");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Deactivate delivery staff (Admin only - soft delete)
        put("/api/delivery-staff/:id/deactivate", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"BOOKSTORE_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int staffId = Integer.parseInt(req.params(":id"));
                boolean success = deliveryStaffDAO.deactivateDeliveryStaff(staffId);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Delivery staff deactivated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to deactivate delivery staff");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Delete delivery staff (Admin only - hard delete)
        delete("/api/delivery-staff/:id", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"BOOKSTORE_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int staffId = Integer.parseInt(req.params(":id"));
                boolean success = deliveryStaffDAO.deleteDeliveryStaff(staffId);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Delivery staff deleted successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to delete delivery staff");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Get available delivery staff (for assignments)
        get("/api/delivery-staff/available", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"BOOKSTORE_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                List<Map<String, Object>> availableStaff = deliveryStaffDAO.getAvailableDeliveryStaff();
                return gson.toJson(availableStaff);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Failed to retrieve available staff: " + e.getMessage()));
            }
        });

        // Get delivery staff performance
        get("/api/delivery-staff/:id/performance", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int staffId = Integer.parseInt(req.params(":id"));
                Map<String, Object> performance = deliveryStaffDAO.getDeliveryStaffPerformance(staffId);

                if (performance != null) {
                    return gson.toJson(performance);
                } else {
                    res.status(404);
                    return gson.toJson(Map.of("status", "error", "message", "Performance data not found"));
                }
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Get current delivery staff profile (for delivery staff dashboard)
        get("/api/delivery-staff/profile", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || !"DELIVERY_STAFF".equals(user.getRole())) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                Map<String, Object> staff = deliveryStaffDAO.getDeliveryStaffByUserId(user.getUserId());
                if (staff != null) {
                    return gson.toJson(staff);
                } else {
                    res.status(404);
                    return gson.toJson(Map.of("status", "error", "message", "Delivery staff profile not found"));
                }
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });
    }
}