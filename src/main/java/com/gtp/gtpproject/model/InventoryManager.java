package com.gtp.gtpproject.model;

public class InventoryManager {
    private int inventoryManagerId;
    private int userId;
    private int managedBooksCount;
    private String department;

    public InventoryManager() {}
    public InventoryManager(int userId, String department) {
        this.userId = userId;
        this.department = department;
        this.managedBooksCount = 0;
    }

    // Getters and Setters
    public int getInventoryManagerId() { return inventoryManagerId; }
    public void setInventoryManagerId(int inventoryManagerId) { this.inventoryManagerId = inventoryManagerId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getManagedBooksCount() { return managedBooksCount; }
    public void setManagedBooksCount(int managedBooksCount) { this.managedBooksCount = managedBooksCount; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}