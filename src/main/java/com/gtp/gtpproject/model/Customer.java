package com.gtp.gtpproject.model;

public class Customer {
    private int customerId;
    private int userId;
    private String preferredGenre;
    private String membershipLevel;
    private int totalOrders;

    public Customer() {}
    public Customer(int userId, String preferredGenre, String membershipLevel) {
        this.userId = userId;
        this.preferredGenre = preferredGenre;
        this.membershipLevel = membershipLevel;
        this.totalOrders = 0;
    }

    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getPreferredGenre() { return preferredGenre; }
    public void setPreferredGenre(String preferredGenre) { this.preferredGenre = preferredGenre; }
    public String getMembershipLevel() { return membershipLevel; }
    public void setMembershipLevel(String membershipLevel) { this.membershipLevel = membershipLevel; }
    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
}