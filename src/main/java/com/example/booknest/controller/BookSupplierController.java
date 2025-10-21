package com.example.booknest.controller;

import com.example.booknest.dao.BookSupplierDAO;
import com.example.booknest.model.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class BookSupplierController {
    private BookSupplierDAO bookSupplierDAO;
    private Gson gson;

    public BookSupplierController() {
        this.bookSupplierDAO = new BookSupplierDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // Get suppliers for a book
        get("/api/book-suppliers/book/:bookId", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int bookId = Integer.parseInt(req.params(":bookId"));
                List<Map<String, Object>> suppliers = bookSupplierDAO.getSuppliersForBook(bookId);
                return gson.toJson(suppliers);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Get books for a supplier
        get("/api/book-suppliers/supplier/:supplierId", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int supplierId = Integer.parseInt(req.params(":supplierId"));
                List<Map<String, Object>> books = bookSupplierDAO.getBooksForSupplier(supplierId);
                return gson.toJson(books);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("status", "error", "message", "Invalid request: " + e.getMessage()));
            }
        });

        // Add book-supplier relationship
        post("/api/book-suppliers", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                boolean success = bookSupplierDAO.addBookSupplier(data);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Book-supplier relationship added successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to add book-supplier relationship");
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

        // Update book-supplier relationship
        put("/api/book-suppliers", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                boolean success = bookSupplierDAO.updateBookSupplier(data);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Book-supplier relationship updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to update book-supplier relationship");
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

        // Remove book-supplier relationship
        delete("/api/book-suppliers", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                int bookId = Integer.parseInt(req.queryParams("bookId"));
                int supplierId = Integer.parseInt(req.queryParams("supplierId"));
                boolean success = bookSupplierDAO.removeBookSupplier(bookId, supplierId);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Book-supplier relationship removed successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to remove book-supplier relationship");
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

        // Set primary supplier for a book
        put("/api/book-suppliers/primary", (req, res) -> {
            res.type("application/json");
            try {
                User user = req.session().attribute("user");
                if (user == null || (!"ADMIN".equals(user.getRole()) && !"INVENTORY_MANAGER".equals(user.getRole()))) {
                    res.status(403);
                    return gson.toJson(Map.of("status", "error", "message", "Access denied"));
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(req.body(), Map.class);
                boolean success = bookSupplierDAO.setPrimarySupplier(data);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Primary supplier set successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to set primary supplier");
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
    }
}
