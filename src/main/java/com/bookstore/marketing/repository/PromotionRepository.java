// PromotionRepository.java
        package com.bookstore.marketing.repository;

import com.bookstore.marketing.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByStatus(String status);
    List<Promotion> findByTargetAudience(String targetAudience);
}