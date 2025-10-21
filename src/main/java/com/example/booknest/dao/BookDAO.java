package com.example.booknest.dao;

import com.example.booknest.model.Book;
import com.example.booknest.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public boolean addBook(Book book) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO books (title, author, isbn, genre, price, stock_quantity, description, publisher, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setDouble(5, book.getPrice());
            pstmt.setInt(6, book.getStockQuantity());
            pstmt.setString(7, book.getDescription());
            pstmt.setString(8, book.getPublisher());
            pstmt.setString(9, book.getImageUrl());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM books WHERE is_active = 1 ORDER BY title";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting books: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return books;
    }

    public List<Book> getBooksByGenre(String genre) {
        List<Book> books = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM books WHERE genre = ? AND is_active = 1 ORDER BY title";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, genre);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting books by genre: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return books;
    }

    public Book getBookById(int bookId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM books WHERE book_id = ? AND is_active = 1";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return null;
    }

    public boolean updateBookStock(int bookId, int newStock) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE books SET stock_quantity = ? WHERE book_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newStock);
            pstmt.setInt(2, bookId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book stock: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean updateBook(Book book) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, genre = ?, price = ?, stock_quantity = ?, description = ?, publisher = ?, image_url = ? WHERE book_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setDouble(5, book.getPrice());
            pstmt.setInt(6, book.getStockQuantity());
            pstmt.setString(7, book.getDescription());
            pstmt.setString(8, book.getPublisher());
            pstmt.setString(9, book.getImageUrl());
            pstmt.setInt(10, book.getBookId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean deleteBook(int bookId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE books SET is_active = 0 WHERE book_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM books WHERE (title LIKE ? OR author LIKE ? OR genre LIKE ?) AND is_active = 1 ORDER BY title";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String searchTerm = "%" + query + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            pstmt.setString(3, searchTerm);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return books;
    }

    public List<String> getAllGenres() {
        List<String> genres = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT DISTINCT genre FROM books WHERE is_active = 1 ORDER BY genre";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                genres.add(rs.getString("genre"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting genres: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return genres;
    }

    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setGenre(rs.getString("genre"));
        book.setPrice(rs.getDouble("price"));
        book.setStockQuantity(rs.getInt("stock_quantity"));
        book.setDescription(rs.getString("description"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublishedDate(rs.getString("published_date"));
        book.setImageUrl(rs.getString("image_url"));
        book.setActive(rs.getBoolean("is_active"));
        return book;
    }
}