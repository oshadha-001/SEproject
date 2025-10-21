package com.example.booknest.controller;

import com.example.booknest.dao.ReviewDAO;
import com.example.booknest.model.Review;
import com.example.booknest.model.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class ReviewController {
    private ReviewDAO reviewDAO;
    private Gson gson;

    public ReviewController() {
        this.reviewDAO = new ReviewDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // Get reviews for a book
        get("/api/reviews/book/:bookId", (req, res) -> {
            res.type("application/json");
            int bookId = Integer.parseInt(req.params(":bookId"));
            List<Review> reviews = reviewDAO.getReviewsByBook(bookId);
            return gson.toJson(reviews);
        });

        // Get reviews by user
        get("/api/reviews/user/:userId", (req, res) -> {
            res.type("application/json");
            int userId = Integer.parseInt(req.params(":userId"));
            List<Review> reviews = reviewDAO.getReviewsByUser(userId);
            return gson.toJson(reviews);
        });

        // Create new review
        post("/api/reviews", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(401);
                    return gson.toJson(Map.of("status", "error", "message", "Please login first"));
                }

                Review review = gson.fromJson(req.body(), Review.class);
                review.setUserId(user.getUserId());

                boolean success = reviewDAO.createReview(review);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Review added successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to add review");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid review data: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        // Update review
        put("/api/reviews/:id", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(401);
                    return gson.toJson(Map.of("status", "error", "message", "Please login first"));
                }

                int reviewId = Integer.parseInt(req.params(":id"));
                Review review = gson.fromJson(req.body(), Review.class);
                review.setReviewId(reviewId);
                review.setUserId(user.getUserId());

                boolean success = reviewDAO.updateReview(review);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Review updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to update review");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid request: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        // Delete review
        delete("/api/reviews/:id", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(401);
                    return gson.toJson(Map.of("status", "error", "message", "Please login first"));
                }

                int reviewId = Integer.parseInt(req.params(":id"));
                boolean success = reviewDAO.deleteReview(reviewId, user.getUserId());
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Review deleted successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to delete review");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid request: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        // Get book rating summary
        get("/api/reviews/book/:bookId/summary", (req, res) -> {
            res.type("application/json");
            int bookId = Integer.parseInt(req.params(":bookId"));
            Map<String, Object> summary = reviewDAO.getBookRatingSummary(bookId);
            return gson.toJson(summary);
        });
    }
}
