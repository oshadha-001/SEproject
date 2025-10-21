package com.example.booknest.dao;

import com.example.booknest.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemLogDAO {
    private Connection connection;

    public SystemLogDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Map<String, Object>> getSystemLogs(String action, String tableName, String startDate, String endDate, int limit) {
        List<Map<String, Object>> logs = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT sl.*, u.first_name, u.last_name FROM system_logs sl " +
                "LEFT JOIN users u ON sl.user_id = u.user_id WHERE 1=1");
        
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (action != null && !action.isEmpty()) {
            sql.append(" AND sl.action LIKE ?");
            params.add("%" + action + "%");
        }
        if (tableName != null && !tableName.isEmpty()) {
            sql.append(" AND sl.table_name = ?");
            params.add(tableName);
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND sl.created_at >= ?");
            params.add(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND sl.created_at <= ?");
            params.add(endDate);
        }

        sql.append(" ORDER BY sl.created_at DESC");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            if (limit > 0) {
                pstmt.setMaxRows(limit);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("logId", rs.getInt("log_id"));
                log.put("userId", rs.getInt("user_id"));
                log.put("action", rs.getString("action"));
                log.put("tableName", rs.getString("table_name"));
                log.put("recordId", rs.getInt("record_id"));
                log.put("oldValues", rs.getString("old_values"));
                log.put("newValues", rs.getString("new_values"));
                log.put("ipAddress", rs.getString("ip_address"));
                log.put("userAgent", rs.getString("user_agent"));
                log.put("createdAt", rs.getTimestamp("created_at").toString());
                log.put("userName", rs.getString("first_name") + " " + rs.getString("last_name"));
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public Map<String, Object> getLogStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        String sql = "SELECT " +
                    "COUNT(*) as total_logs, " +
                    "COUNT(DISTINCT user_id) as unique_users, " +
                    "COUNT(CASE WHEN created_at >= DATEADD(day, -7, GETDATE()) THEN 1 END) as logs_last_week, " +
                    "COUNT(CASE WHEN created_at >= DATEADD(day, -30, GETDATE()) THEN 1 END) as logs_last_month " +
                    "FROM system_logs";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                statistics.put("totalLogs", rs.getInt("total_logs"));
                statistics.put("uniqueUsers", rs.getInt("unique_users"));
                statistics.put("logsLastWeek", rs.getInt("logs_last_week"));
                statistics.put("logsLastMonth", rs.getInt("logs_last_month"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statistics;
    }

    public List<Map<String, Object>> getUserActivityLogs(int userId) {
        List<Map<String, Object>> logs = new ArrayList<>();
        String sql = "SELECT * FROM system_logs WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("logId", rs.getInt("log_id"));
                log.put("userId", rs.getInt("user_id"));
                log.put("action", rs.getString("action"));
                log.put("tableName", rs.getString("table_name"));
                log.put("recordId", rs.getInt("record_id"));
                log.put("oldValues", rs.getString("old_values"));
                log.put("newValues", rs.getString("new_values"));
                log.put("ipAddress", rs.getString("ip_address"));
                log.put("userAgent", rs.getString("user_agent"));
                log.put("createdAt", rs.getTimestamp("created_at").toString());
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public boolean cleanupOldLogs(int daysToKeep) {
        String sql = "DELETE FROM system_logs WHERE created_at < DATEADD(day, -?, GETDATE())";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, daysToKeep);
            return pstmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
