package com.example.booknest.controller;

import com.example.booknest.dao.InventoryDAO;
import com.example.booknest.service.InventoryService;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class InventoryController {
    private InventoryDAO inventoryDAO;
    private InventoryService inventoryService;
    private Gson gson;

    public InventoryController() {
        this.inventoryDAO = new InventoryDAO();
        this.inventoryService = new InventoryService();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        get("/api/inventory/low-stock", (req, res) -> {
            res.type("application/json");
            int threshold = Integer.parseInt(req.queryParams("threshold") != null ?
                    req.queryParams("threshold") : "5");
            List<Map<String, Object>> lowStockItems = inventoryDAO.getLowStockItems(threshold);
            return gson.toJson(lowStockItems);
        });

        get("/api/inventory/summary", (req, res) -> {
            res.type("application/json");
            Map<String, Object> summary = inventoryDAO.getInventorySummary();
            return gson.toJson(summary);
        });

        // Restock book
        post("/api/inventory/restock", (req, res) -> {
            res.type("application/json");
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                int bookId = ((Number) data.get("bookId")).intValue();
                int quantity = ((Number) data.get("quantity")).intValue();

                Map<String, Object> response = inventoryService.restockBook(bookId, quantity);
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid request: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        // Get inventory health
        get("/api/inventory/health", (req, res) -> {
            res.type("application/json");
            Map<String, Object> health = inventoryService.getInventoryHealth();
            return gson.toJson(health);
        });
    }
}