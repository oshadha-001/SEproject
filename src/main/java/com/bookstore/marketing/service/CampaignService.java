package com.bookstore.marketing.service;

import com.bookstore.marketing.entity.Campaign;
import com.bookstore.marketing.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    // Create new campaign
    public Campaign createCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    // Get all campaigns
    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    // Get campaign by ID
    public Optional<Campaign> getCampaignById(Long id) {
        return campaignRepository.findById(id);
    }

    // Get active campaigns
    public List<Campaign> getActiveCampaigns() {
        return campaignRepository.findByActive(true);
    }

    // Get current campaigns (active and within date range)
    public List<Campaign> getCurrentCampaigns() {
        LocalDate today = LocalDate.now();
        return campaignRepository.findByStartDateBeforeAndEndDateAfter(today, today);
    }

    // Get campaigns by type
    public List<Campaign> getCampaignsByType(String campaignType) {
        return campaignRepository.findByCampaignType(campaignType);
    }

    // Update campaign
    public Campaign updateCampaign(Long id, Campaign updatedCampaign) {
        return campaignRepository.findById(id)
                .map(campaign -> {
                    campaign.setName(updatedCampaign.getName());
                    campaign.setDescription(updatedCampaign.getDescription());
                    campaign.setDiscountPercentage(updatedCampaign.getDiscountPercentage());
                    campaign.setStartDate(updatedCampaign.getStartDate());
                    campaign.setEndDate(updatedCampaign.getEndDate());
                    campaign.setCampaignType(updatedCampaign.getCampaignType());
                    campaign.setActive(updatedCampaign.getActive());
                    return campaignRepository.save(campaign);
                })
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
    }

    // Delete campaign
    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }

    // Toggle campaign active status
    public Campaign toggleCampaignStatus(Long id) {
        return campaignRepository.findById(id)
                .map(campaign -> {
                    campaign.setActive(!campaign.getActive());
                    return campaignRepository.save(campaign);
                })
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
    }

    public Object findAll() {
    return null;
    }
}