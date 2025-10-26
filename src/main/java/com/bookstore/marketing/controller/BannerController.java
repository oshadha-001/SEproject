package com.bookstore.marketing.controller;

import com.bookstore.marketing.entity.Banner;
import com.bookstore.marketing.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional; // <--- CRITICAL FIX: Missing import for java.util.Optional

@Controller
@RequestMapping("/banners")
public class BannerController {

    // CORRECTED: Use constructor injection (recommended practice) instead of field injection
    private final BannerService bannerService;

    @Autowired
    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping
    public String listBanners(Model model) {
        model.addAttribute("banners", bannerService.getAllBanners());
        return "banners/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("banner", new Banner());
        return "banners/form";
    }

    @PostMapping("/save")
    public String saveBanner(@ModelAttribute Banner banner, RedirectAttributes redirectAttributes) {
        try {
            bannerService.saveBanner(banner);
            redirectAttributes.addFlashAttribute("success", "Banner saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/banners";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // FIX: 'optional' changed to 'Optional' (correct capitalization)
        Optional<Banner> banner = bannerService.getBannerById(id);

        // FIX: Correct methods are now available after import and capitalization fix
        if (banner.isPresent()) {
            model.addAttribute("banner", banner.get());
            return "banners/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Banner not found!");
            return "redirect:/banners";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBanner(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bannerService.deleteBanner(id);
            redirectAttributes.addFlashAttribute("success", "Banner deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting banner!");
        }
        return "redirect:/banners";
    }

    // NEW: View banner details
    @GetMapping("/view/{id}")
    public String viewBanner(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // FIX: Correct methods are now available after import
        Optional<Banner> banner = bannerService.getBannerById(id);
        if (banner.isPresent()) {
            model.addAttribute("banner", banner.get());
            return "banners/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Banner not found!");
            return "redirect:/banners";
        }
    }
}