package com.example.booknest.dao;

import com.example.booknest.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailCampaignDAO {
    private Connection connection;

    public EmailCampaignDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Map<String, Object>> getAllCampaigns() {
        List<Map<String, Object>> campaigns = new ArrayList<>();
        String sql = "SELECT * FROM email_campaigns ORDER BY created_at DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> campaign = new HashMap<>();
                campaign.put("campaignId", rs.getInt("campaign_id"));
                campaign.put("campaignName", rs.getString("campaign_name"));
                campaign.put("subject", rs.getString("subject"));
                campaign.put("content", rs.getString("content"));
                campaign.put("targetAudience", rs.getString("target_audience"));
                campaign.put("sentCount", rs.getInt("sent_count"));
                campaign.put("openedCount", rs.getInt("opened_count"));
                campaign.put("clickedCount", rs.getInt("clicked_count"));
                campaign.put("createdAt", rs.getTimestamp("created_at").toString());
                campaign.put("sentAt", rs.getTimestamp("sent_at") != null ? rs.getTimestamp("sent_at").toString() : null);
                campaign.put("active", rs.getBoolean("is_active"));
                campaigns.add(campaign);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaigns;
    }

    public boolean createCampaign(Map<String, Object> data) {
        String sql = "INSERT INTO email_campaigns (campaign_name, subject, content, target_audience) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, (String) data.get("campaignName"));
            pstmt.setString(2, (String) data.get("subject"));
            pstmt.setString(3, (String) data.get("content"));
            pstmt.setString(4, (String) data.get("targetAudience"));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCampaign(int campaignId, Map<String, Object> data) {
        String sql = "UPDATE email_campaigns SET campaign_name = ?, subject = ?, content = ?, target_audience = ? WHERE campaign_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, (String) data.get("campaignName"));
            pstmt.setString(2, (String) data.get("subject"));
            pstmt.setString(3, (String) data.get("content"));
            pstmt.setString(4, (String) data.get("targetAudience"));
            pstmt.setInt(5, campaignId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendCampaign(int campaignId) {
        String sql = "UPDATE email_campaigns SET sent_at = GETDATE(), sent_count = sent_count + 1 WHERE campaign_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, campaignId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> getCampaignAnalytics(int campaignId) {
        Map<String, Object> analytics = new HashMap<>();
        String sql = "SELECT sent_count, opened_count, clicked_count, " +
                    "CASE WHEN sent_count > 0 THEN (opened_count * 100.0 / sent_count) ELSE 0 END as open_rate, " +
                    "CASE WHEN sent_count > 0 THEN (clicked_count * 100.0 / sent_count) ELSE 0 END as click_rate " +
                    "FROM email_campaigns WHERE campaign_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, campaignId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                analytics.put("sentCount", rs.getInt("sent_count"));
                analytics.put("openedCount", rs.getInt("opened_count"));
                analytics.put("clickedCount", rs.getInt("clicked_count"));
                analytics.put("openRate", rs.getDouble("open_rate"));
                analytics.put("clickRate", rs.getDouble("click_rate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return analytics;
    }

    public boolean deleteCampaign(int campaignId) {
        String sql = "UPDATE email_campaigns SET is_active = 0 WHERE campaign_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, campaignId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
