package com.example.booknest.controller;

import com.example.booknest.dao.SupplierDAO;
import com.example.booknest.model.Supplier;
import com.example.booknest.model.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class SupplierController {
    private SupplierDAO supplierDAO;
    private Gson gson;

    public SupplierController() {
        this.supplierDAO = new SupplierDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // Get all suppliers
        get("/api/suppliers", (req, res) -> {
            res.type("application/json");
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            return gson.toJson(suppliers);
        });

        // Get supplier by ID
        get("/api/suppliers/:id", (req, res) -> {
            res.type("application/json");
            int supplierId = Integer.parseInt(req.params(":id"));
            Supplier supplier = supplierDAO.getSupplierById(supplierId);
            if (supplier != null) {
                return gson.toJson(supplier);
            } else {
                res.status(404);
                return gson.toJson(Map.of("status", "error", "message", "Supplier not found"));
            }
        });

        // Create new supplier
        post("/api/suppliers", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                Supplier supplier = gson.fromJson(req.body(), Supplier.class);
                boolean success = supplierDAO.createSupplier(supplier);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Supplier created successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to create supplier");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid supplier data: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        // Update supplier
        put("/api/suppliers/:id", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int supplierId = Integer.parseInt(req.params(":id"));
                Supplier supplier = gson.fromJson(req.body(), Supplier.class);
                supplier.setSupplierId(supplierId);

                boolean success = supplierDAO.updateSupplier(supplier);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Supplier updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to update supplier");
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

        // Delete supplier
        delete("/api/suppliers/:id", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || !"ADMIN".equals(user.getRole())) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int supplierId = Integer.parseInt(req.params(":id"));
                boolean success = supplierDAO.deleteSupplier(supplierId);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Supplier deleted successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to delete supplier");
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

        // Get supplier performance
        get("/api/suppliers/:id/performance", (req, res) -> {
            res.type("application/json");
            int supplierId = Integer.parseInt(req.params(":id"));
            Map<String, Object> performance = supplierDAO.getSupplierPerformance(supplierId);
            return gson.toJson(performance);
        });
    }
}
