package com.example.booknest.dao;

import com.example.booknest.model.DeliveryStaff;
import com.example.booknest.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryStaffDAO {

    public boolean addDeliveryStaff(DeliveryStaff staff) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO delivery_staff (user_id, vehicle_type, license_number, is_active) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, staff.getUserId());
            pstmt.setString(2, staff.getVehicleType());
            pstmt.setString(3, staff.getLicenseNumber());
            pstmt.setBoolean(4, staff.isActive());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding delivery staff: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<Map<String, Object>> getAllDeliveryStaff() {
        List<Map<String, Object>> staffList = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT ds.*, u.first_name, u.last_name, u.email, u.phone " +
                    "FROM delivery_staff ds " +
                    "INNER JOIN users u ON ds.user_id = u.user_id " +
                    "WHERE ds.is_active = 1 " +
                    "ORDER BY u.first_name, u.last_name";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> staff = new HashMap<>();
                staff.put("staffId", rs.getInt("staff_id"));
                staff.put("userId", rs.getInt("user_id"));
                staff.put("firstName", rs.getString("first_name"));
                staff.put("lastName", rs.getString("last_name"));
                staff.put("email", rs.getString("email"));
                staff.put("phone", rs.getString("phone"));
                staff.put("vehicleType", rs.getString("vehicle_type"));
                staff.put("licenseNumber", rs.getString("license_number"));
                staff.put("isActive", rs.getBoolean("is_active"));
                staff.put("createdAt", rs.getString("created_at"));
                staffList.add(staff);
            }
        } catch (SQLException e) {
            System.err.println("Error getting delivery staff: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return staffList;
    }

    public Map<String, Object> getDeliveryStaffById(int staffId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT ds.*, u.first_name, u.last_name, u.email, u.phone " +
                    "FROM delivery_staff ds " +
                    "INNER JOIN users u ON ds.user_id = u.user_id " +
                    "WHERE ds.staff_id = ? AND ds.is_active = 1";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> staff = new HashMap<>();
                staff.put("staffId", rs.getInt("staff_id"));
                staff.put("userId", rs.getInt("user_id"));
                staff.put("firstName", rs.getString("first_name"));
                staff.put("lastName", rs.getString("last_name"));
                staff.put("email", rs.getString("email"));
                staff.put("phone", rs.getString("phone"));
                staff.put("vehicleType", rs.getString("vehicle_type"));
                staff.put("licenseNumber", rs.getString("license_number"));
                staff.put("isActive", rs.getBoolean("is_active"));
                staff.put("createdAt", rs.getString("created_at"));
                return staff;
            }
        } catch (SQLException e) {
            System.err.println("Error getting delivery staff by ID: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return null;
    }

    public Map<String, Object> getDeliveryStaffByUserId(int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT ds.*, u.first_name, u.last_name, u.email, u.phone " +
                    "FROM delivery_staff ds " +
                    "INNER JOIN users u ON ds.user_id = u.user_id " +
                    "WHERE ds.user_id = ? AND ds.is_active = 1";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> staff = new HashMap<>();
                staff.put("staffId", rs.getInt("staff_id"));
                staff.put("userId", rs.getInt("user_id"));
                staff.put("firstName", rs.getString("first_name"));
                staff.put("lastName", rs.getString("last_name"));
                staff.put("email", rs.getString("email"));
                staff.put("phone", rs.getString("phone"));
                staff.put("vehicleType", rs.getString("vehicle_type"));
                staff.put("licenseNumber", rs.getString("license_number"));
                staff.put("isActive", rs.getBoolean("is_active"));
                staff.put("createdAt", rs.getString("created_at"));
                return staff;
            }
        } catch (SQLException e) {
            System.err.println("Error getting delivery staff by user ID: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return null;
    }

    public boolean updateDeliveryStaff(DeliveryStaff staff) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE delivery_staff SET vehicle_type = ?, license_number = ? WHERE staff_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, staff.getVehicleType());
            pstmt.setString(2, staff.getLicenseNumber());
            pstmt.setInt(3, staff.getStaffId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating delivery staff: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean deactivateDeliveryStaff(int staffId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE delivery_staff SET is_active = 0 WHERE staff_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, staffId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating delivery staff: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean deleteDeliveryStaff(int staffId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM delivery_staff WHERE staff_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, staffId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting delivery staff: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<Map<String, Object>> getAvailableDeliveryStaff() {
        List<Map<String, Object>> staffList = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT ds.*, u.first_name, u.last_name " +
                    "FROM delivery_staff ds " +
                    "INNER JOIN users u ON ds.user_id = u.user_id " +
                    "WHERE ds.is_active = 1 " +
                    "AND ds.staff_id NOT IN (" +
                    "    SELECT delivery_staff_id FROM delivery_assignments " +
                    "    WHERE status IN ('ASSIGNED', 'IN_PROGRESS')" +
                    ") " +
                    "ORDER BY u.first_name, u.last_name";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> staff = new HashMap<>();
                staff.put("staffId", rs.getInt("staff_id"));
                staff.put("userId", rs.getInt("user_id"));
                staff.put("firstName", rs.getString("first_name"));
                staff.put("lastName", rs.getString("last_name"));
                staff.put("vehicleType", rs.getString("vehicle_type"));
                staff.put("licenseNumber", rs.getString("license_number"));
                staffList.add(staff);
            }
        } catch (SQLException e) {
            System.err.println("Error getting available delivery staff: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return staffList;
    }

    public Map<String, Object> getDeliveryStaffPerformance(int staffId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            // Get delivery statistics
            String sql = "SELECT " +
                    "COUNT(*) as total_assignments, " +
                    "COUNT(CASE WHEN status = 'DELIVERED' THEN 1 END) as completed_deliveries, " +
                    "COUNT(CASE WHEN status = 'FAILED' THEN 1 END) as failed_deliveries, " +
                    "AVG(CASE WHEN status = 'DELIVERED' THEN DATEDIFF(HOUR, assigned_date, completed_at) END) as avg_delivery_time " +
                    "FROM delivery_assignments " +
                    "WHERE delivery_staff_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> performance = new HashMap<>();
                performance.put("totalAssignments", rs.getInt("total_assignments"));
                performance.put("completedDeliveries", rs.getInt("completed_deliveries"));
                performance.put("failedDeliveries", rs.getInt("failed_deliveries"));
                performance.put("avgDeliveryTime", rs.getDouble("avg_delivery_time"));
                performance.put("successRate", rs.getInt("total_assignments") > 0 ?
                        (rs.getInt("completed_deliveries") * 100.0 / rs.getInt("total_assignments")) : 0);
                return performance;
            }
        } catch (SQLException e) {
            System.err.println("Error getting delivery staff performance: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return null;
    }
}