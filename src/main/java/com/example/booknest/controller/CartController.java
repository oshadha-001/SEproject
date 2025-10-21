package com.example.booknest.controller;

import com.example.booknest.model.User;
import com.example.booknest.service.CartService;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class CartController {
    private CartService cartService;
    private Gson gson;

    public CartController() {
        this.cartService = new CartService();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // Add item to cart
        post("/api/cart/add", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(401);
                    return gson.toJson(Map.of("status", "error", "message", "Please login first"));
                }

                Map<String, Object> body = gson.fromJson(req.body(), Map.class);
                int bookId = ((Number) body.get("bookId")).intValue();
                int quantity = ((Number) body.get("quantity")).intValue();

                Map<String, Object> response = cartService.addToCart(user.getUserId(), bookId, quantity);
                return gson.toJson(response);

            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid request: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        // Get cart items
        get("/api/cart", (req, res) -> {
            res.type("application/json");
            User user = req.session().attribute("user");
            if (user == null) {
                res.status(401);
                return gson.toJson(Map.of("status", "error", "message", "Please login first"));
            }

            var cartItems = cartService.getCartItems(user.getUserId());
            var cartSummary = cartService.getCartSummary(user.getUserId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("cartItems", cartItems);
            response.put("summary", cartSummary);

            return gson.toJson(response);
        });

        // Update cart item quantity
        put("/api/cart/update", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(401);
                    return gson.toJson(Map.of("status", "error", "message", "Please login first"));
                }

                Map<String, Object> body = gson.fromJson(req.body(), Map.class);
                int cartItemId = ((Number) body.get("cartItemId")).intValue();
                int quantity = ((Number) body.get("quantity")).intValue();

                Map<String, Object> response = cartService.updateCartItem(cartItemId, quantity, user.getUserId());
                return gson.toJson(response);

            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid request: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        // Remove item from cart
        delete("/api/cart/remove/:id", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null) {
                    res.status(401);
                    return gson.toJson(Map.of("status", "error", "message", "Please login first"));
                }

                int cartItemId = Integer.parseInt(req.params(":id"));
                Map<String, Object> response = cartService.removeFromCart(cartItemId);
                return gson.toJson(response);

            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid request: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        // Get cart summary
        get("/api/cart/summary", (req, res) -> {
            res.type("application/json");
            User user = req.session().attribute("user");
            if (user == null) {
                res.status(401);
                return gson.toJson(Map.of("status", "error", "message", "Please login first"));
            }

            Map<String, Object> response = cartService.getCartSummary(user.getUserId());
            response.put("status", "success");
            return gson.toJson(response);
        });

        // Clear cart
        delete("/api/cart/clear", (req, res) -> {
            res.type("application/json");
            User user = req.session().attribute("user");
            if (user == null) {
                res.status(401);
                return gson.toJson(Map.of("status", "error", "message", "Please login first"));
            }

            boolean success = cartService.clearCart(user.getUserId());
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("status", "success");
                response.put("message", "Cart cleared successfully");
            } else {
                response.put("status", "error");
                response.put("message", "Failed to clear cart");
            }
            return gson.toJson(response);
        });
    }
}