package com.bookstore.marketing.repository;

import com.bookstore.marketing.entity.Analytics;
import com.bookstore.marketing.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {

    // Custom query method to find the Analytics record linked to a Promotion ID
    Optional<Analytics> findByPromotionId(Long promotionId);

    Optional<Analytics> findByPromotion(Promotion promotion);
}