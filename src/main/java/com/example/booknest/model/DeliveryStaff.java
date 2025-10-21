package com.example.booknest.model;

public class DeliveryStaff {
    private int deliveryStaffId;
    private int userId;
    private String vehicleType;
    private String deliveryZone;
    private int deliveriesCompleted;

    public DeliveryStaff() {}
    public DeliveryStaff(int userId, String vehicleType, String deliveryZone) {
        this.userId = userId;
        this.vehicleType = vehicleType;
        this.deliveryZone = deliveryZone;
        this.deliveriesCompleted = 0;
    }

    // Getters and Setters
    public int getDeliveryStaffId() { return deliveryStaffId; }
    public void setDeliveryStaffId(int deliveryStaffId) { this.deliveryStaffId = deliveryStaffId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getDeliveryZone() { return deliveryZone; }
    public void setDeliveryZone(String deliveryZone) { this.deliveryZone = deliveryZone; }
    public int getDeliveriesCompleted() { return deliveriesCompleted; }
    public void setDeliveriesCompleted(int deliveriesCompleted) { this.deliveriesCompleted = deliveriesCompleted; }

    public void setLicenseNumber(String licenseNumber) {
    }

    public void setStaffId(int staffId) {
    }

    public String getLicenseNumber() {

        return "";
    }

    public boolean isActive() {
        return false;
    }
}