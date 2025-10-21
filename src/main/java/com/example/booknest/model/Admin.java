package com.example.booknest.model;

public class Admin {
    private int adminId;
    private int userId;
    private String adminLevel;

    public Admin() {}
    public Admin(int userId, String adminLevel) {
        this.userId = userId;
        this.adminLevel = adminLevel;
    }

    // Getters and Setters
    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
}