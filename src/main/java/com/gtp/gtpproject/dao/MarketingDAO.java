package com.gtp.gtpproject.dao;

import com.example.booknest.model.Promotion;
import com.example.booknest.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketingDAO {

    public boolean createPromotion(Promotion promotion) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO promotions (promotion_name, description, discount_percentage, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, promotion.getPromotionName());
            pstmt.setString(2, promotion.getDescription());
            pstmt.setDouble(3, promotion.getDiscountPercentage());
            pstmt.setString(4, promotion.getStartDate());
            pstmt.setString(5, promotion.getEndDate());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating promotion: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<Promotion> getActivePromotions() {
        List<Promotion> promotions = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM promotions WHERE is_active = 1 AND end_date >= GETDATE() ORDER BY start_date DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                promotions.add(extractPromotionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active promotions: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return promotions;
    }

    public List<Promotion> getAllPromotions() {
        List<Promotion> promotions = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM promotions ORDER BY created_at DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                promotions.add(extractPromotionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all promotions: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return promotions;
    }

    public Promotion getPromotionById(int promotionId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM promotions WHERE promotion_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, promotionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractPromotionFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting promotion by ID: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return null;
    }

    public boolean updatePromotion(Promotion promotion) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE promotions SET promotion_name = ?, description = ?, discount_percentage = ?, start_date = ?, end_date = ?, is_active = ? WHERE promotion_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, promotion.getPromotionName());
            pstmt.setString(2, promotion.getDescription());
            pstmt.setDouble(3, promotion.getDiscountPercentage());
            pstmt.setString(4, promotion.getStartDate());
            pstmt.setString(5, promotion.getEndDate());
            pstmt.setBoolean(6, promotion.isActive());
            pstmt.setInt(7, promotion.getPromotionId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating promotion: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean deactivatePromotion(int promotionId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE promotions SET is_active = 0 WHERE promotion_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, promotionId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating promotion: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public Map<String, Object> getPromotionPerformance(int promotionId) {
        Map<String, Object> performance = new HashMap<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // This would typically join with orders to get actual performance data
            // For now, return mock data with some calculations
            String sql = "SELECT COUNT(DISTINCT o.order_id) as orders_count, " +
                    "SUM(o.total_amount) as total_revenue " +
                    "FROM orders o " +
                    "WHERE o.promotion_id = ? AND o.status = 'DELIVERED'";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, promotionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                performance.put("ordersCount", rs.getInt("orders_count"));
                performance.put("totalRevenue", rs.getDouble("total_revenue"));
            } else {
                performance.put("ordersCount", 0);
                performance.put("totalRevenue", 0.0);
            }

        } catch (SQLException e) {
            System.err.println("Error getting promotion performance: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return performance;
    }

    private Promotion extractPromotionFromResultSet(ResultSet rs) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setPromotionId(rs.getInt("promotion_id"));
        promotion.setPromotionName(rs.getString("promotion_name"));
        promotion.setDescription(rs.getString("description"));
        promotion.setDiscountPercentage(rs.getDouble("discount_percentage"));
        promotion.setStartDate(rs.getString("start_date"));
        promotion.setEndDate(rs.getString("end_date"));
        promotion.setActive(rs.getBoolean("is_active"));
        return promotion;
    }
}