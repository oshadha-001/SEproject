package com.example.booknest.dao;

import com.example.booknest.model.CartItem;
import com.example.booknest.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    public boolean addToCart(int userId, int bookId, int quantity) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            // Check if item already exists in cart
            String checkSql = "SELECT cart_item_id, quantity FROM cart_items WHERE user_id = ? AND book_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Update existing item
                int existingQuantity = rs.getInt("quantity");
                int cartItemId = rs.getInt("cart_item_id");
                String updateSql = "UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, existingQuantity + quantity);
                updateStmt.setInt(2, cartItemId);
                return updateStmt.executeUpdate() > 0;
            } else {
                // Insert new item
                String insertSql = "INSERT INTO cart_items (user_id, book_id, quantity) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, bookId);
                insertStmt.setInt(3, quantity);
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error adding to cart: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<CartItem> getCartItems(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT ci.cart_item_id, ci.user_id, ci.book_id, ci.quantity, ci.added_date, " +
                    "b.title, b.author, b.price, b.image_url " +
                    "FROM cart_items ci " +
                    "JOIN books b ON ci.book_id = b.book_id " +
                    "WHERE ci.user_id = ? " +
                    "ORDER BY ci.added_date DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemId(rs.getInt("cart_item_id"));
                item.setUserId(rs.getInt("user_id"));
                item.setBookId(rs.getInt("book_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setAddedDate(rs.getTimestamp("added_date"));
                item.setBookTitle(rs.getString("title"));
                item.setAuthor(rs.getString("author"));
                item.setPrice(rs.getDouble("price"));
                item.setImageUrl(rs.getString("image_url"));
                cartItems.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting cart items: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return cartItems;
    }

    public boolean updateCartItemQuantity(int cartItemId, int quantity) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, cartItemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating cart item: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean removeFromCart(int cartItemId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM cart_items WHERE cart_item_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cartItemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing from cart: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean clearCart(int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM cart_items WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public int getCartItemCount(int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) as item_count FROM cart_items WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("item_count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting cart item count: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return 0;
    }
}