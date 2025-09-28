package com.gtp.gtpproject.service;

import com.example.booknest.dao.BookDAO;
import com.example.booknest.model.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookService {
    private BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAO();
    }

    public Map<String, Object> addBook(Book book) {
        Map<String, Object> response = new HashMap<>();

        // Validate book data
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Book title is required");
            return response;
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Author is required");
            return response;
        }

        if (book.getPrice() <= 0) {
            response.put("status", "error");
            response.put("message", "Price must be greater than 0");
            return response;
        }

        if (book.getStockQuantity() < 0) {
            response.put("status", "error");
            response.put("message", "Stock quantity cannot be negative");
            return response;
        }

        boolean success = bookDAO.addBook(book);
        if (success) {
            response.put("status", "success");
            response.put("message", "Book added successfully");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to add book");
        }

        return response;
    }

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    public List<Book> getBooksByGenre(String genre) {
        return bookDAO.getBooksByGenre(genre);
    }

    public Book getBookById(int bookId) {
        return bookDAO.getBookById(bookId);
    }

    public Map<String, Object> updateBookStock(int bookId, int newStock) {
        Map<String, Object> response = new HashMap<>();

        if (newStock < 0) {
            response.put("status", "error");
            response.put("message", "Stock quantity cannot be negative");
            return response;
        }

        boolean success = bookDAO.updateBookStock(bookId, newStock);
        if (success) {
            response.put("status", "success");
            response.put("message", "Stock updated successfully");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to update stock");
        }

        return response;
    }

    public Map<String, Object> searchBooks(String query) {
        Map<String, Object> response = new HashMap<>();
        // This would call a search method in BookDAO
        // For now, we'll search through all books
        List<Book> allBooks = getAllBooks();
        List<Book> searchResults = allBooks.stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                                book.getGenre().toLowerCase().contains(query.toLowerCase()))
                .toList();

        response.put("status", "success");
        response.put("books", searchResults);
        return response;
    }
}