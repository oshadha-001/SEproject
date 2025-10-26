package com.bookstore.marketing.service;

import com.bookstore.marketing.entity.Analytics;
import com.bookstore.marketing.entity.Promotion;
import com.bookstore.marketing.repository.AnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    // Get all analytics reports
    public List<Analytics> getAllAnalytics() {
        return analyticsRepository.findAll();
    }

    // Get analytics by ID
    public Optional<Analytics> getAnalyticsById(Long id) {
        return analyticsRepository.findById(id);
    }

    // Get analytics for specific promotion
    public Optional<Analytics> getAnalyticsByPromotion(Promotion promotion) {
        return analyticsRepository.findByPromotion(promotion);
    }

    // Calculate average open rate across all campaigns
    public Double getAverageOpenRate() {
        List<Analytics> allAnalytics = analyticsRepository.findAll();
        if (allAnalytics.isEmpty()) {
            return 0.0;
        }
        return allAnalytics.stream()
                .mapToDouble(Analytics::getOpenRate)
                .average()
                .orElse(0.0);
    }

    // Calculate average click-through rate
    public Double getAverageClickThroughRate() {
        List<Analytics> allAnalytics = analyticsRepository.findAll();
        if (allAnalytics.isEmpty()) {
            return 0.0;
        }
        return allAnalytics.stream()
                .mapToDouble(Analytics::getClickThroughRate)
                .average()
                .orElse(0.0);
    }

    // Get total emails delivered
    public Integer getTotalEmailsDelivered() {
        return analyticsRepository.findAll().stream()
                .mapToInt(Analytics::getEmailsDelivered)
                .sum();
    }

    // Get total emails opened
    public Integer getTotalEmailsOpened() {
        return analyticsRepository.findAll().stream()
                .mapToInt(Analytics::getEmailsOpened)
                .sum();
    }

    // Get campaign performance summary
    public String getPerformanceSummary(Long analyticsId) {
        return analyticsRepository.findById(analyticsId)
                .map(analytics -> {
                    StringBuilder summary = new StringBuilder();
                    summary.append("Campaign Performance Summary\n");
                    summary.append("============================\n");
                    summary.append("Promotion: ").append(analytics.getPromotion().getSubject()).append("\n");
                    summary.append("Emails Delivered: ").append(analytics.getEmailsDelivered()).append("\n");
                    summary.append("Emails Opened: ").append(analytics.getEmailsOpened()).append("\n");
                    summary.append("Links Clicked: ").append(analytics.getLinksClicked()).append("\n");
                    summary.append("Unsubscribes: ").append(analytics.getUnsubscribes()).append("\n");
                    summary.append("Open Rate: ").append(String.format("%.2f", analytics.getOpenRate())).append("%\n");
                    summary.append("Click-Through Rate: ").append(String.format("%.2f", analytics.getClickThroughRate())).append("%\n");
                    summary.append("Unsubscribe Rate: ").append(String.format("%.2f", analytics.getUnsubscribeRate())).append("%\n");
                    return summary.toString();
                })
                .orElse("Analytics not found");
    }
}