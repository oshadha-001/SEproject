package com.example.booknest.controller;

import com.example.booknest.dao.BookDAO;
import com.example.booknest.model.Book;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class BookController {
    private BookDAO bookDAO;
    private Gson gson;

    public BookController() {
        this.bookDAO = new BookDAO();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        get("/api/books", (req, res) -> {
            res.type("application/json");
            List<Book> books = bookDAO.getAllBooks();
            return gson.toJson(books);
        });

        get("/api/books/genre/:genre", (req, res) -> {
            res.type("application/json");
            String genre = req.params(":genre");
            List<Book> books = bookDAO.getBooksByGenre(genre);
            return gson.toJson(books);
        });

        get("/api/books/:id", (req, res) -> {
            res.type("application/json");
            int bookId = Integer.parseInt(req.params(":id"));
            Book book = bookDAO.getBookById(bookId);

            if (book != null) {
                return gson.toJson(book);
            } else {
                res.status(404);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Book not found");
                return gson.toJson(response);
            }
        });

        post("/api/books", (req, res) -> {
            res.type("application/json");
            try {
                Book book = gson.fromJson(req.body(), Book.class);
                boolean success = bookDAO.addBook(book);

                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Book added successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to add book");
                }
                return gson.toJson(response);
            } catch (Exception e) {
                res.status(400);
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Invalid book data: " + e.getMessage());
                return gson.toJson(response);
            }
        });

        put("/api/books/:id/stock", (req, res) -> {
            res.type("application/json");
            try {
                int bookId = Integer.parseInt(req.params(":id"));
                Map<String, Integer> data = gson.fromJson(req.body(), Map.class);
                int newStock = data.get("stock");

                boolean success = bookDAO.updateBookStock(bookId, newStock);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Stock updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to update stock");
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

        // Update book
        put("/api/books/:id", (req, res) -> {
            res.type("application/json");
            try {
                int bookId = Integer.parseInt(req.params(":id"));
                Book book = gson.fromJson(req.body(), Book.class);
                book.setBookId(bookId);
                
                boolean success = bookDAO.updateBook(book);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Book updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to update book");
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

        // Delete book
        delete("/api/books/:id", (req, res) -> {
            res.type("application/json");
            try {
                int bookId = Integer.parseInt(req.params(":id"));
                boolean success = bookDAO.deleteBook(bookId);
                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("status", "success");
                    response.put("message", "Book deleted successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to delete book");
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

        // Search books
        get("/api/books/search", (req, res) -> {
            res.type("application/json");
            String query = req.queryParams("q");
            if (query == null || query.trim().isEmpty()) {
                List<Book> books = bookDAO.getAllBooks();
                return gson.toJson(books);
            }
            List<Book> books = bookDAO.searchBooks(query);
            return gson.toJson(books);
        });

        // Get all genres
        get("/api/books/genres", (req, res) -> {
            res.type("application/json");
            List<String> genres = bookDAO.getAllGenres();
            return gson.toJson(genres);
        });
    }
}