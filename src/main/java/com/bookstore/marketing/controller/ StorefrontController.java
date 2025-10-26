package com.bookstore.marketing.controller;

import com.bookstore.marketing.entity.Banner;
import com.bookstore.marketing.entity.Campaign;
import com.bookstore.marketing.service.BannerService;
import com.bookstore.marketing.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
class StorefrontController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private BannerService bannerService;

    // Customer-facing storefront page
    @GetMapping("/storefront")
    public String storefront(Model model) {

        // Get ACTIVE campaigns that are currently running (within date range)
        LocalDate today = LocalDate.now();
        List<Campaign> activeCampaigns = campaignService.getActiveCampaigns().stream()
                .filter(campaign ->
                        !campaign.getStartDate().isAfter(today) &&
                                !campaign.getEndDate().isBefore(today))
                .collect(Collectors.toList());

        // Get ACTIVE banners
        List<Banner> activeBanners = bannerService.getActiveBanners();

        // Separate banners by type
        List<Banner> popupBanners = activeBanners.stream()
                .filter(banner -> "POPUP".equals(banner.getType()))
                .collect(Collectors.toList());

        List<Banner> topBanners = activeBanners.stream()
                .filter(banner -> "BANNER".equals(banner.getType()) && "TOP".equals(banner.getPosition()))
                .collect(Collectors.toList());

        List<Banner> bottomBanners = activeBanners.stream()
                .filter(banner -> "BANNER".equals(banner.getType()) && "BOTTOM".equals(banner.getPosition()))
                .collect(Collectors.toList());

        List<Banner> sidebarBanners = activeBanners.stream()
                .filter(banner -> "BANNER".equals(banner.getType()) && "SIDEBAR".equals(banner.getPosition()))
                .collect(Collectors.toList());

        // Add to model
        model.addAttribute("campaigns", activeCampaigns);
        model.addAttribute("popupBanners", popupBanners);
        model.addAttribute("topBanners", topBanners);
        model.addAttribute("bottomBanners", bottomBanners);
        model.addAttribute("sidebarBanners", sidebarBanners);

        return "storefront";
    }
}