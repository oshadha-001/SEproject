package com.gtp.gtpproject.controller;

import com.example.booknest.dao.MarketingDAO;
import com.example.booknest.model.Promotion;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class MarketingController {
    private MarketingDAO marketingDAO;
    private Gson gson;

    public MarketingController() {
        this.marketingDAO = new MarketingDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
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