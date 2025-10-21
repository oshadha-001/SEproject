package com.example.booknest.controller;

import com.example.booknest.dao.MarketingDAO;
import com.example.booknest.model.Promotion;
import com.example.booknest.service.MarketingService;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class MarketingController {
    private MarketingDAO marketingDAO;
    private MarketingService marketingService;
    private Gson gson;

    public MarketingController() {
        this.marketingDAO = new MarketingDAO();
        this.marketingService = new MarketingService();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // Create promotion
        post("/api/marketing/promotions", (req, res) -> {
            res.type("application/json");
            try {
                Promotion promotion = gson.fromJson(req.body(), Promotion.class);
                Map<String, Object> response = marketingService.createPromotion(promotion);
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid promotion data: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        // Get all promotions
        get("/api/marketing/promotions", (req, res) -> {
            res.type("application/json");
            List<Promotion> promotions = marketingDAO.getAllPromotions();
            return gson.toJson(promotions);
        });

        // Get active promotions
        get("/api/marketing/promotions/active", (req, res) -> {
            res.type("application/json");
            List<Promotion> promotions = marketingDAO.getActivePromotions();
            return gson.toJson(promotions);
        });

        // Update promotion
        put("/api/marketing/promotions/:id", (req, res) -> {
            res.type("application/json");
            try {
                int promotionId = Integer.parseInt(req.params(":id"));
                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                
                boolean success = marketingDAO.updatePromotion(promotionId, data);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Promotion updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to update promotion");
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

        // Delete promotion
        delete("/api/marketing/promotions/:id", (req, res) -> {
            res.type("application/json");
            try {
                int promotionId = Integer.parseInt(req.params(":id"));
                boolean success = marketingDAO.deactivatePromotion(promotionId);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Promotion deleted successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to delete promotion");
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

        // Get marketing analytics
        get("/api/marketing/analytics", (req, res) -> {
            res.type("application/json");
            Map<String, Object> analytics = marketingService.getMarketingAnalytics();
            return gson.toJson(analytics);
        });

        // Legacy endpoints for backward compatibility
        post("/api/promotions", (req, res) -> {
            res.type("application/json");
            try {
                Promotion promotion = gson.fromJson(req.body(), Promotion.class);
                boolean success = marketingDAO.createPromotion(promotion);

                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Promotion created successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to create promotion");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid promotion data: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        get("/api/promotions/active", (req, res) -> {
            res.type("application/json");
            List<Promotion> promotions = marketingDAO.getActivePromotions();
            return gson.toJson(promotions);
        });
    }
}