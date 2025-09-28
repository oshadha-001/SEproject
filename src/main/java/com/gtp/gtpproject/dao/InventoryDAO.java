package com.gtp.gtpproject.dao;

import com.example.booknest.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryDAO {

    public List<Map<String, Object>> getLowStockItems(int threshold) {
        List<Map<String, Object>> lowStockItems = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT book_id, title, stock_quantity FROM books WHERE stock_quantity <= ? AND is_active = 1 ORDER BY stock_quantity ASC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, threshold);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("bookId", rs.getInt("book_id"));
                item.put("title", rs.getString("title"));
                item.put("stockQuantity", rs.getInt("stock_quantity"));
                lowStockItems.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting low stock items: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return lowStockItems;
    }

    public Map<String, Object> getInventorySummary() {
        Map<String, Object> summary = new HashMap<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            // Total books
            String sql1 = "SELECT COUNT(*) as total_books FROM books WHERE is_active = 1";
            PreparedStatement pstmt1 = conn.prepareStatement(sql1);
            ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next()) {
                summary.put("totalBooks", rs1.getInt("total_books"));
            }

            // Total value
            String sql2 = "SELECT SUM(price * stock_quantity) as total_value FROM books WHERE is_active = 1";
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next()) {
                summary.put("totalValue", rs2.getDouble("total_value"));
            }

            // Out of stock items
            String sql3 = "SELECT COUNT(*) as out_of_stock FROM books WHERE stock_quantity = 0 AND is_active = 1";
            PreparedStatement pstmt3 = conn.prepareStatement(sql3);
            ResultSet rs3 = pstmt3.executeQuery();
            if (rs3.next()) {
                summary.put("outOfStock", rs3.getInt("out_of_stock"));
            }

            // Low stock items (less than 5)
            String sql4 = "SELECT COUNT(*) as low_stock FROM books WHERE stock_quantity > 0 AND stock_quantity <= 5 AND is_active = 1";
            PreparedStatement pstmt4 = conn.prepareStatement(sql4);
            ResultSet rs4 = pstmt4.executeQuery();
            if (rs4.next()) {
                summary.put("lowStock", rs4.getInt("low_stock"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting inventory summary: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return summary;
    }

    public List<Map<String, Object>> getInventoryMovement() {
        List<Map<String, Object>> movement = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // This would typically join with order_items to track inventory movement
            // For now, return a simplified version
            String sql = "SELECT b.book_id, b.title, b.stock_quantity, " +
                    "(SELECT SUM(oi.quantity) FROM order_items oi " +
                    "JOIN orders o ON oi.order_id = o.order_id " +
                    "WHERE oi.book_id = b.book_id AND o.order_date >= DATEADD(day, -30, GETDATE())) as sold_last_30_days " +
                    "FROM books b WHERE b.is_active = 1 ORDER BY sold_last_30_days DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("bookId", rs.getInt("book_id"));
                item.put("title", rs.getString("title"));
                item.put("currentStock", rs.getInt("stock_quantity"));
                item.put("soldLast30Days", rs.getInt("sold_last_30_days"));
                movement.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting inventory movement: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return movement;
    }

    public Map<String, Object> getCategorySummary() {
        Map<String, Object> categorySummary = new HashMap<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT genre, COUNT(*) as book_count, SUM(stock_quantity) as total_stock, " +
                    "SUM(price * stock_quantity) as total_value " +
                    "FROM books WHERE is_active = 1 GROUP BY genre ORDER BY total_value DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            List<Map<String, Object>> categories = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> category = new HashMap<>();
                category.put("genre", rs.getString("genre"));
                category.put("bookCount", rs.getInt("book_count"));
                category.put("totalStock", rs.getInt("total_stock"));
                category.put("totalValue", rs.getDouble("total_value"));
                categories.add(category);
            }

            categorySummary.put("categories", categories);

        } catch (SQLException e) {
            System.err.println("Error getting category summary: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return categorySummary;
    }
}