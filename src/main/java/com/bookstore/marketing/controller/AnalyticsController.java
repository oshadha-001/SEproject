package com.bookstore.marketing.controller;

import com.bookstore.marketing.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // List all analytics reports
    @GetMapping
    public String listAnalytics(Model model) {
        model.addAttribute("analytics", analyticsService.getAllAnalytics());
        model.addAttribute("avgOpenRate", analyticsService.getAverageOpenRate());
        model.addAttribute("avgClickRate", analyticsService.getAverageClickThroughRate());
        model.addAttribute("totalDelivered", analyticsService.getTotalEmailsDelivered());
        model.addAttribute("totalOpened", analyticsService.getTotalEmailsOpened());
        return "analytics/list";
    }

    // View detailed analytics report
    @GetMapping("/view/{id}")
    public String viewAnalytics(@PathVariable Long id, Model model) {
        return analyticsService.getAnalyticsById(id)
                .map(analytics -> {
                    model.addAttribute("analytics", analytics);
                    return "analytics/detail";
                })
                .orElse("redirect:/analytics");
    }
}