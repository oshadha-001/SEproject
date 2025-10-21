package com.example.booknest.controller;

import com.example.booknest.dao.EmailCampaignDAO;
import com.example.booknest.model.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class EmailCampaignController {
    private EmailCampaignDAO emailCampaignDAO;
    private Gson gson;

    public EmailCampaignController() {
        this.emailCampaignDAO = new EmailCampaignDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // Get all email campaigns
        get("/api/email-campaigns", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"MARKETING_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                List<Map<String, Object>> campaigns = emailCampaignDAO.getAllCampaigns();
                return gson.toJson(campaigns);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Create new email campaign
        post("/api/email-campaigns", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"MARKETING_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                boolean success = emailCampaignDAO.createCampaign(data);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Email campaign created successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to create email campaign");
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

        // Update email campaign
        put("/api/email-campaigns/:id", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"MARKETING_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int campaignId = Integer.parseInt(req.params(":id"));
                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                boolean success = emailCampaignDAO.updateCampaign(campaignId, data);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Email campaign updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to update email campaign");
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

        // Send email campaign
        post("/api/email-campaigns/:id/send", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"MARKETING_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int campaignId = Integer.parseInt(req.params(":id"));
                boolean success = emailCampaignDAO.sendCampaign(campaignId);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Email campaign sent successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to send email campaign");
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

        // Get campaign analytics
        get("/api/email-campaigns/:id/analytics", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"MARKETING_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int campaignId = Integer.parseInt(req.params(":id"));
                Map<String, Object> analytics = emailCampaignDAO.getCampaignAnalytics(campaignId);
                return gson.toJson(analytics);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Delete email campaign
        delete("/api/email-campaigns/:id", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || !"ADMIN".equals(user.getRole())) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int campaignId = Integer.parseInt(req.params(":id"));
                boolean success = emailCampaignDAO.deleteCampaign(campaignId);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Email campaign deleted successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to delete email campaign");
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
