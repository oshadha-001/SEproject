package com.gtp.gtpproject.controller;

import com.example.booknest.dao.InventoryDAO;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class InventoryController {
    private InventoryDAO inventoryDAO;
    private Gson gson;

    public InventoryController() {
        this.inventoryDAO = new InventoryDAO();
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
    }
}