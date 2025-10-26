package com.bookstore.marketing.service;

import com.bookstore.marketing.entity.Banner;
import com.bookstore.marketing.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    @Autowired
    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    public Optional<Banner> getBannerById(Long id) {
        return bannerRepository.findById(id);
    }

    public Banner saveBanner(Banner banner) {
        // If the checkbox is unchecked, 'active' comes as null. Set it to false.
        if (banner.getActive() == null) {
            banner.setActive(false);
        }
        return bannerRepository.save(banner);
    }

    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    /**
     * Finds banners that are active and within their date range, handling null dates.
     */
    public List<Banner> getActiveBanners() {
        // 1. Fetch only the banners marked as 'active = true' from the database
        List<Banner> activeBannersFromDb = bannerRepository.findByActiveTrue();

        // Safety check
        if (activeBannersFromDb == null || activeBannersFromDb.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDate today = LocalDate.now();

        // 2. Filter the list based on the date range, with NULL CHECKS
        return activeBannersFromDb.stream()
                .filter(banner -> {
                    LocalDate startDate = banner.getStartDate();
                    LocalDate endDate = banner.getEndDate();

                    // CRITICAL: Exclude banners if either date is NULL.
                    if (startDate == null || endDate == null) {
                        return false;
                    }

                    // Check if today is NOT before the start date AND NOT after the end date
                    boolean isNotBeforeStart = !startDate.isAfter(today);
                    boolean isNotAfterEnd = !endDate.isBefore(today);

                    return isNotBeforeStart && isNotAfterEnd;
                })
                .collect(Collectors.toList());
    }
}