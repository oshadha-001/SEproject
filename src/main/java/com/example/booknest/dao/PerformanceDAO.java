package com.example.booknest.dao;

import com.example.booknest.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceDAO {
    private Connection connection;

    public PerformanceDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Map<String, Object>> getPerformanceRecords(int userId) {
        List<Map<String, Object>> records = new ArrayList<>();
        String sql = "SELECT pr.*, u.first_name, u.last_name FROM performance_records pr " +
                    "JOIN users u ON pr.user_id = u.user_id " +
                    "WHERE pr.user_id = ? " +
                    "ORDER BY pr.performance_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("recordId", rs.getInt("record_id"));
                record.put("userId", rs.getInt("user_id"));
                record.put("performanceDate", rs.getDate("performance_date").toString());
                record.put("tasksCompleted", rs.getInt("tasks_completed"));
                record.put("efficiencyScore", rs.getDouble("efficiency_score"));
                record.put("notes", rs.getString("notes"));
                record.put("createdAt", rs.getTimestamp("created_at").toString());
                record.put("userName", rs.getString("first_name") + " " + rs.getString("last_name"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public boolean addPerformanceRecord(Map<String, Object> data) {
        String sql = "INSERT INTO performance_records (user_id, performance_date, tasks_completed, efficiency_score, notes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, (Integer) data.get("userId"));
            pstmt.setDate(2, Date.valueOf((String) data.get("performanceDate")));
            pstmt.setInt(3, (Integer) data.get("tasksCompleted"));
            pstmt.setDouble(4, ((Number) data.get("efficiencyScore")).doubleValue());
            pstmt.setString(5, (String) data.get("notes"));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePerformanceRecord(int recordId, Map<String, Object> data) {
        String sql = "UPDATE performance_records SET performance_date = ?, tasks_completed = ?, efficiency_score = ?, notes = ? WHERE record_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf((String) data.get("performanceDate")));
            pstmt.setInt(2, (Integer) data.get("tasksCompleted"));
            pstmt.setDouble(3, ((Number) data.get("efficiencyScore")).doubleValue());
            pstmt.setString(4, (String) data.get("notes"));
            pstmt.setInt(5, recordId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> getPerformanceAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        String sql = "SELECT " +
                    "COUNT(*) as total_records, " +
                    "AVG(efficiency_score) as avg_efficiency, " +
                    "AVG(tasks_completed) as avg_tasks, " +
                    "MAX(efficiency_score) as max_efficiency, " +
                    "MIN(efficiency_score) as min_efficiency " +
                    "FROM performance_records";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                analytics.put("totalRecords", rs.getInt("total_records"));
                analytics.put("averageEfficiency", rs.getDouble("avg_efficiency"));
                analytics.put("averageTasks", rs.getDouble("avg_tasks"));
                analytics.put("maxEfficiency", rs.getDouble("max_efficiency"));
                analytics.put("minEfficiency", rs.getDouble("min_efficiency"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return analytics;
    }

    public List<Map<String, Object>> getTopPerformers(int limit) {
        List<Map<String, Object>> performers = new ArrayList<>();
        String sql = "SELECT TOP (?) u.user_id, u.first_name, u.last_name, u.role, " +
                    "AVG(pr.efficiency_score) as avg_efficiency, " +
                    "SUM(pr.tasks_completed) as total_tasks " +
                    "FROM users u " +
                    "JOIN performance_records pr ON u.user_id = pr.user_id " +
                    "WHERE u.role != 'CUSTOMER' " +
                    "GROUP BY u.user_id, u.first_name, u.last_name, u.role " +
                    "ORDER BY AVG(pr.efficiency_score) DESC, SUM(pr.tasks_completed) DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> performer = new HashMap<>();
                performer.put("userId", rs.getInt("user_id"));
                performer.put("userName", rs.getString("first_name") + " " + rs.getString("last_name"));
                performer.put("role", rs.getString("role"));
                performer.put("averageEfficiency", rs.getDouble("avg_efficiency"));
                performer.put("totalTasks", rs.getInt("total_tasks"));
                performers.add(performer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return performers;
    }
}
