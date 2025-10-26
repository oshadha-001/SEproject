package com.bookstore.marketing.repository;

import com.bookstore.marketing.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    // This method is required by BannerService.getActiveBanners()
    List<Banner> findByActiveTrue();
}