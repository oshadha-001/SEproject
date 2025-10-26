// CampaignRepository.java
package com.bookstore.marketing.repository;

import com.bookstore.marketing.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByActive(Boolean active);
    List<Campaign> findByStartDateBeforeAndEndDateAfter(LocalDate start, LocalDate end);
    List<Campaign> findByCampaignType(String campaignType);
}