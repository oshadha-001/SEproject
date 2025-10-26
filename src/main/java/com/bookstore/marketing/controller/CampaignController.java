package com.bookstore.marketing.controller;

import com.bookstore.marketing.entity.Campaign;
import com.bookstore.marketing.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/campaigns")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    // List all campaigns
    @GetMapping
    public String listCampaigns(Model model) {
        model.addAttribute("campaigns", campaignService.getAllCampaigns());
        return "campaigns/list";
    }

    // Show create campaign form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("campaign", new Campaign());
        return "campaigns/form";
    }

    // Create new campaign
    @PostMapping
    public String createCampaign(@Valid @ModelAttribute Campaign campaign,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "campaigns/form";
        }

        campaignService.createCampaign(campaign);
        redirectAttributes.addFlashAttribute("success", "Campaign created successfully!");
        return "redirect:/campaigns";
    }

    // Show edit campaign form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return campaignService.getCampaignById(id)
                .map(campaign -> {
                    model.addAttribute("campaign", campaign);
                    return "campaigns/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Campaign not found!");
                    return "redirect:/campaigns";
                });
    }

    // Update campaign
    @PostMapping("/update/{id}")
    public String updateCampaign(@PathVariable Long id,
                                 @Valid @ModelAttribute Campaign campaign,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "campaigns/form";
        }

        try {
            campaignService.updateCampaign(id, campaign);
            redirectAttributes.addFlashAttribute("success", "Campaign updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/campaigns";
    }

    // Delete campaign
    @GetMapping("/delete/{id}")
    public String deleteCampaign(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            campaignService.deleteCampaign(id);
            redirectAttributes.addFlashAttribute("success", "Campaign deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete campaign!");
        }
        return "redirect:/campaigns";
    }

    // Toggle campaign status
    @GetMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            campaignService.toggleCampaignStatus(id);
            redirectAttributes.addFlashAttribute("success", "Campaign status updated!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/campaigns";
    }

    // NEW: View campaign details
    @GetMapping("/view/{id}")
    public String viewCampaign(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return campaignService.getCampaignById(id)
                .map(campaign -> {
                    model.addAttribute("campaign", campaign);
                    return "campaigns/view";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Campaign not found!");
                    return "redirect:/campaigns";
                });
    }
}