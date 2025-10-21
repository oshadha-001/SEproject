package com.example.booknest.service;

import com.example.booknest.dao.MarketingDAO;
import com.example.booknest.model.Promotion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketingService {
    private MarketingDAO marketingDAO;

    public MarketingService() {
        this.marketingDAO = new MarketingDAO();
    }

    public Map<String, Object> createPromotion(Promotion promotion) {
        Map<String, Object> response = new HashMap<>();

        // Validate promotion data
        if (promotion.getPromotionName() == null || promotion.getPromotionName().trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Promotion name is required");
            return response;
        }

        if (promotion.getDiscountPercentage() <= 0 || promotion.getDiscountPercentage() > 100) {
            response.put("status", "error");
            response.put("message", "Discount percentage must be between 1 and 100");
            return response;
        }

        if (promotion.getStartDate() == null || promotion.getEndDate() == null) {
            response.put("status", "error");
            response.put("message", "Start and end dates are required");
            return response;
        }

        // Validate date logic
        if (promotion.getEndDate().compareTo(promotion.getStartDate()) < 0) {
            response.put("status", "error");
            response.put("message", "End date cannot be before start date");
            return response;
        }

        boolean success = marketingDAO.createPromotion(promotion);
        if (success) {
            response.put("status", "success");
            response.put("message", "Promotion created successfully");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to create promotion");
        }

        return response;
    }

    public List<Promotion> getActivePromotions() {
        return marketingDAO.getActivePromotions();
    }

    public Map<String, Object> getPromotionPerformance(int promotionId) {
        Map<String, Object> performance = new HashMap<>();

        // This would typically query campaign performance data
        // For now, we'll return mock data
        performance.put("promotionId", promotionId);
        performance.put("totalViews", 15000);
        performance.put("clicks", 1200);
        performance.put("conversions", 85);
        performance.put("conversionRate", 7.08);
        performance.put("revenueGenerated", 4250.00);
        performance.put("roi", 212.5);

        return performance;
    }

    public Map<String, Object> getMarketingAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        List<Promotion> activePromotions = getActivePromotions();
        List<Promotion> allPromotions = marketingDAO.getAllPromotions();

        // Calculate analytics
        int activePromotionsCount = activePromotions.size();
        int totalPromotions = allPromotions.size();
        
        // Calculate average discount
        double avgDiscount = allPromotions.stream()
                .mapToDouble(Promotion::getDiscountPercentage)
                .average()
                .orElse(0.0);

        // Calculate campaigns this month (mock data)
        int campaignsThisMonth = (int) (totalPromotions * 0.3);

        // Mock data for other metrics
        String topPromotion = allPromotions.isEmpty() ? "-" : allPromotions.get(0).getPromotionName();
        double totalDiscount = allPromotions.stream()
                .mapToDouble(Promotion::getDiscountPercentage)
                .sum();
        double conversionRate = 5.2; // Mock conversion rate

        analytics.put("activePromotions", activePromotionsCount);
        analytics.put("totalPromotions", totalPromotions);
        analytics.put("avgDiscount", avgDiscount);
        analytics.put("campaignsThisMonth", campaignsThisMonth);
        analytics.put("topPromotion", topPromotion);
        analytics.put("totalDiscount", totalDiscount);
        analytics.put("conversionRate", conversionRate);

        return analytics;
    }
}