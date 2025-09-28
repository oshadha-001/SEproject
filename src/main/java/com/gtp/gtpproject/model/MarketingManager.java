package com.gtp.gtpproject.model;

public class MarketingManager {
    private int marketingManagerId;
    private int userId;
    private String department;
    private int activeCampaigns;

    public MarketingManager() {}
    public MarketingManager(int userId, String department) {
        this.userId = userId;
        this.department = department;
        this.activeCampaigns = 0;
    }

    // Getters and Setters
    public int getMarketingManagerId() { return marketingManagerId; }
    public void setMarketingManagerId(int marketingManagerId) { this.marketingManagerId = marketingManagerId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getActiveCampaigns() { return activeCampaigns; }
    public void setActiveCampaigns(int activeCampaigns) { this.activeCampaigns = activeCampaigns; }
}