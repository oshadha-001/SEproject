package com.example.booknest.dao;

import com.example.booknest.model.Review;
import com.example.booknest.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewDAO {
    private Connection connection;

    public ReviewDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Review> getReviewsByBook(int bookId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.first_name, u.last_name FROM reviews r " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "WHERE r.book_id = ? AND r.is_active = 1 " +
                    "ORDER BY r.created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setBookId(rs.getInt("book_id"));
                review.setUserId(rs.getInt("user_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));
                review.setCreatedAt(rs.getTimestamp("created_at").toString());
                review.setActive(rs.getBoolean("is_active"));
                review.setUserName(rs.getString("first_name") + " " + rs.getString("last_name"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public List<Review> getReviewsByUser(int userId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, b.title FROM reviews r " +
                    "JOIN books b ON r.book_id = b.book_id " +
                    "WHERE r.user_id = ? AND r.is_active = 1 " +
                    "ORDER BY r.created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setBookId(rs.getInt("book_id"));
                review.setUserId(rs.getInt("user_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));
                review.setCreatedAt(rs.getTimestamp("created_at").toString());
                review.setActive(rs.getBoolean("is_active"));
                review.setBookTitle(rs.getString("title"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public boolean createReview(Review review) {
        String sql = "INSERT INTO reviews (book_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, review.getBookId());
            pstmt.setInt(2, review.getUserId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateReview(Review review) {
        String sql = "UPDATE reviews SET rating = ?, comment = ? WHERE review_id = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, review.getRating());
            pstmt.setString(2, review.getComment());
            pstmt.setInt(3, review.getReviewId());
            pstmt.setInt(4, review.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReview(int reviewId, int userId) {
        String sql = "UPDATE reviews SET is_active = 0 WHERE review_id = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> getBookRatingSummary(int bookId) {
        Map<String, Object> summary = new HashMap<>();
        String sql = "SELECT " +
                    "COUNT(*) as total_reviews, " +
                    "AVG(CAST(rating AS FLOAT)) as average_rating, " +
                    "COUNT(CASE WHEN rating = 5 THEN 1 END) as five_star, " +
                    "COUNT(CASE WHEN rating = 4 THEN 1 END) as four_star, " +
                    "COUNT(CASE WHEN rating = 3 THEN 1 END) as three_star, " +
                    "COUNT(CASE WHEN rating = 2 THEN 1 END) as two_star, " +
                    "COUNT(CASE WHEN rating = 1 THEN 1 END) as one_star " +
                    "FROM reviews WHERE book_id = ? AND is_active = 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                summary.put("totalReviews", rs.getInt("total_reviews"));
                summary.put("averageRating", rs.getDouble("average_rating"));
                summary.put("fiveStar", rs.getInt("five_star"));
                summary.put("fourStar", rs.getInt("four_star"));
                summary.put("threeStar", rs.getInt("three_star"));
                summary.put("twoStar", rs.getInt("two_star"));
                summary.put("oneStar", rs.getInt("one_star"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }
}
