package com.example.booknest.dao;

import com.example.booknest.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryAssignmentDAO {
    private Connection connection;

    public DeliveryAssignmentDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Map<String, Object>> getAssignmentsByStaff(int staffId) {
        List<Map<String, Object>> assignments = new ArrayList<>();
        String sql = "SELECT da.*, o.order_id, o.total_amount, o.shipping_address, " +
                    "u.first_name AS customer_first_name, u.last_name AS customer_last_name " +
                    "FROM delivery_assignments da " +
                    "JOIN orders o ON da.order_id = o.order_id " +
                    "JOIN users u ON o.customer_id = u.user_id " +
                    "WHERE da.delivery_staff_id = ? " +
                    "ORDER BY da.assigned_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> assignment = new HashMap<>();
                assignment.put("assignmentId", rs.getInt("assignment_id"));
                assignment.put("orderId", rs.getInt("order_id"));
                assignment.put("deliveryStaffId", rs.getInt("delivery_staff_id"));
                assignment.put("assignedDate", rs.getTimestamp("assigned_date").toString());
                assignment.put("deliveryDate", rs.getDate("delivery_date") != null ? rs.getDate("delivery_date").toString() : null);
                assignment.put("status", rs.getString("status"));
                assignment.put("deliveryNotes", rs.getString("delivery_notes"));
                assignment.put("completedAt", rs.getTimestamp("completed_at") != null ? rs.getTimestamp("completed_at").toString() : null);
                assignment.put("totalAmount", rs.getDouble("total_amount"));
                assignment.put("shippingAddress", rs.getString("shipping_address"));
                assignment.put("customerName", rs.getString("customer_first_name") + " " + rs.getString("customer_last_name"));
                assignments.add(assignment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    public List<Map<String, Object>> getAllAssignments() {
        List<Map<String, Object>> assignments = new ArrayList<>();
        String sql = "SELECT da.*, o.order_id, o.total_amount, o.shipping_address, " +
                    "u.first_name AS customer_first_name, u.last_name AS customer_last_name, " +
                    "ds.first_name as staff_first_name, ds.last_name as staff_last_name " +
                    "FROM delivery_assignments da " +
                    "JOIN orders o ON da.order_id = o.order_id " +
                    "JOIN users u ON o.customer_id = u.user_id " +
                    "JOIN users ds ON da.delivery_staff_id = ds.user_id " +
                    "ORDER BY da.assigned_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> assignment = new HashMap<>();
                assignment.put("assignmentId", rs.getInt("assignment_id"));
                assignment.put("orderId", rs.getInt("order_id"));
                assignment.put("deliveryStaffId", rs.getInt("delivery_staff_id"));
                assignment.put("assignedDate", rs.getTimestamp("assigned_date").toString());
                assignment.put("deliveryDate", rs.getDate("delivery_date") != null ? rs.getDate("delivery_date").toString() : null);
                assignment.put("status", rs.getString("status"));
                assignment.put("deliveryNotes", rs.getString("delivery_notes"));
                assignment.put("completedAt", rs.getTimestamp("completed_at") != null ? rs.getTimestamp("completed_at").toString() : null);
                assignment.put("totalAmount", rs.getDouble("total_amount"));
                assignment.put("shippingAddress", rs.getString("shipping_address"));
                assignment.put("customerName", rs.getString("customer_first_name") + " " + rs.getString("customer_last_name"));
                assignment.put("staffName", rs.getString("staff_first_name") + " " + rs.getString("staff_last_name"));
                assignments.add(assignment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    public boolean assignDelivery(Map<String, Object> data) {
        String sql = "INSERT INTO delivery_assignments (order_id, delivery_staff_id, delivery_date, status, delivery_notes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, (Integer) data.get("orderId"));
            pstmt.setInt(2, (Integer) data.get("deliveryStaffId"));
            pstmt.setDate(3, Date.valueOf((String) data.get("deliveryDate")));
            pstmt.setString(4, (String) data.get("status"));
            pstmt.setString(5, (String) data.get("deliveryNotes"));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDeliveryStatus(int assignmentId, String status, String notes, int userId) {
        String sql = "UPDATE delivery_assignments SET status = ?, delivery_notes = ? WHERE assignment_id = ? AND delivery_staff_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, notes);
            pstmt.setInt(3, assignmentId);
            pstmt.setInt(4, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean completeDelivery(int assignmentId, String deliveryNotes, int userId) {
        String sql = "UPDATE delivery_assignments SET status = 'COMPLETED', delivery_notes = ?, completed_at = GETDATE() WHERE assignment_id = ? AND delivery_staff_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, deliveryNotes);
            pstmt.setInt(2, assignmentId);
            pstmt.setInt(3, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> getDeliveryAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        String sql = "SELECT " +
                    "COUNT(*) as total_assignments, " +
                    "COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed, " +
                    "COUNT(CASE WHEN status = 'IN_PROGRESS' THEN 1 END) as in_progress, " +
                    "COUNT(CASE WHEN status = 'ASSIGNED' THEN 1 END) as assigned, " +
                    "AVG(DATEDIFF(day, assigned_date, completed_at)) as avg_delivery_time " +
                    "FROM delivery_assignments " +
                    "WHERE completed_at IS NOT NULL";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                analytics.put("totalAssignments", rs.getInt("total_assignments"));
                analytics.put("completed", rs.getInt("completed"));
                analytics.put("inProgress", rs.getInt("in_progress"));
                analytics.put("assigned", rs.getInt("assigned"));
                analytics.put("averageDeliveryTime", rs.getDouble("avg_delivery_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return analytics;
    }
}
