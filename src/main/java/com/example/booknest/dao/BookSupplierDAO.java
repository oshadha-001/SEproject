package com.example.booknest.dao;

import com.example.booknest.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookSupplierDAO {
    private Connection connection;

    public BookSupplierDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Map<String, Object>> getSuppliersForBook(int bookId) {
        List<Map<String, Object>> suppliers = new ArrayList<>();
        String sql = "SELECT bs.*, s.supplier_name, s.contact_person, s.email, s.phone " +
                    "FROM book_suppliers bs " +
                    "JOIN suppliers s ON bs.supplier_id = s.supplier_id " +
                    "WHERE bs.book_id = ? AND s.is_active = 1 " +
                    "ORDER BY bs.is_primary DESC, bs.supplier_price ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> supplier = new HashMap<>();
                supplier.put("bookId", rs.getInt("book_id"));
                supplier.put("supplierId", rs.getInt("supplier_id"));
                supplier.put("supplierName", rs.getString("supplier_name"));
                supplier.put("contactPerson", rs.getString("contact_person"));
                supplier.put("email", rs.getString("email"));
                supplier.put("phone", rs.getString("phone"));
                supplier.put("supplierPrice", rs.getDouble("supplier_price"));
                supplier.put("leadTimeDays", rs.getInt("lead_time_days"));
                supplier.put("isPrimary", rs.getBoolean("is_primary"));
                supplier.put("createdAt", rs.getTimestamp("created_at").toString());
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public List<Map<String, Object>> getBooksForSupplier(int supplierId) {
        List<Map<String, Object>> books = new ArrayList<>();
        String sql = "SELECT bs.*, b.title, b.author, b.genre, b.price " +
                    "FROM book_suppliers bs " +
                    "JOIN books b ON bs.book_id = b.book_id " +
                    "WHERE bs.supplier_id = ? AND b.is_active = 1 " +
                    "ORDER BY bs.is_primary DESC, b.title ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> book = new HashMap<>();
                book.put("bookId", rs.getInt("book_id"));
                book.put("supplierId", rs.getInt("supplier_id"));
                book.put("title", rs.getString("title"));
                book.put("author", rs.getString("author"));
                book.put("genre", rs.getString("genre"));
                book.put("retailPrice", rs.getDouble("price"));
                book.put("supplierPrice", rs.getDouble("supplier_price"));
                book.put("leadTimeDays", rs.getInt("lead_time_days"));
                book.put("isPrimary", rs.getBoolean("is_primary"));
                book.put("createdAt", rs.getTimestamp("created_at").toString());
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public boolean addBookSupplier(Map<String, Object> data) {
        String sql = "INSERT INTO book_suppliers (book_id, supplier_id, supplier_price, lead_time_days, is_primary) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, (Integer) data.get("bookId"));
            pstmt.setInt(2, (Integer) data.get("supplierId"));
            pstmt.setDouble(3, ((Number) data.get("supplierPrice")).doubleValue());
            pstmt.setInt(4, (Integer) data.get("leadTimeDays"));
            pstmt.setBoolean(5, (Boolean) data.get("isPrimary"));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBookSupplier(Map<String, Object> data) {
        String sql = "UPDATE book_suppliers SET supplier_price = ?, lead_time_days = ?, is_primary = ? WHERE book_id = ? AND supplier_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, ((Number) data.get("supplierPrice")).doubleValue());
            pstmt.setInt(2, (Integer) data.get("leadTimeDays"));
            pstmt.setBoolean(3, (Boolean) data.get("isPrimary"));
            pstmt.setInt(4, (Integer) data.get("bookId"));
            pstmt.setInt(5, (Integer) data.get("supplierId"));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeBookSupplier(int bookId, int supplierId) {
        String sql = "DELETE FROM book_suppliers WHERE book_id = ? AND supplier_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, supplierId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setPrimarySupplier(Map<String, Object> data) {
        try {
            connection.setAutoCommit(false);
            
            // First, set all suppliers for this book to non-primary
            String clearPrimarySql = "UPDATE book_suppliers SET is_primary = 0 WHERE book_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(clearPrimarySql)) {
                pstmt.setInt(1, (Integer) data.get("bookId"));
                pstmt.executeUpdate();
            }
            
            // Then set the specified supplier as primary
            String setPrimarySql = "UPDATE book_suppliers SET is_primary = 1 WHERE book_id = ? AND supplier_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(setPrimarySql)) {
                pstmt.setInt(1, (Integer) data.get("bookId"));
                pstmt.setInt(2, (Integer) data.get("supplierId"));
                boolean success = pstmt.executeUpdate() > 0;
                connection.commit();
                return success;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
