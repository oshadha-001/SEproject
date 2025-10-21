package com.example.booknest.service;

import com.example.booknest.dao.BookDAO;
import com.example.booknest.dao.CartDAO;
import com.example.booknest.model.CartItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartService {
    private CartDAO cartDAO;
    private BookDAO bookDAO;

    public CartService() {
        this.cartDAO = new CartDAO();
        this.bookDAO = new BookDAO();
    }

    public Map<String, Object> addToCart(int userId, int bookId, int quantity) {
        Map<String, Object> response = new HashMap<>();

        // Validate input
        if (quantity <= 0) {
            response.put("status", "error");
            response.put("message", "Quantity must be greater than 0");
            return response;
        }

        // Check if book exists and has stock
        var book = bookDAO.getBookById(bookId);
        if (book == null || !book.isActive()) {
            response.put("status", "error");
            response.put("message", "Book not available");
            return response;
        }

        if (book.getStockQuantity() < quantity) {
            response.put("status", "error");
            response.put("message", "Insufficient stock. Only " + book.getStockQuantity() + " available");
            return response;
        }

        // Add to cart
        boolean success = cartDAO.addToCart(userId, bookId, quantity);
        if (success) {
            response.put("status", "success");
            response.put("message", "Book added to cart successfully");
            response.put("cartItemCount", cartDAO.getCartItemCount(userId));
        } else {
            response.put("status", "error");
            response.put("message", "Failed to add book to cart");
        }

        return response;
    }

    public List<CartItem> getCartItems(int userId) {
        return cartDAO.getCartItems(userId);
    }

    public Map<String, Object> updateCartItem(int cartItemId, int quantity, int userId) {
        Map<String, Object> response = new HashMap<>();

        if (quantity <= 0) {
            // If quantity is 0 or negative, remove the item
            boolean success = cartDAO.removeFromCart(cartItemId);
            if (success) {
                response.put("status", "success");
                response.put("message", "Item removed from cart");
            } else {
                response.put("status", "error");
                response.put("message", "Failed to remove item from cart");
            }
            return response;
        }

        // Validate stock for the updated quantity
        var cartItems = cartDAO.getCartItems(userId);
        var targetItem = cartItems.stream()
                .filter(item -> item.getCartItemId() == cartItemId)
                .findFirst()
                .orElse(null);

        if (targetItem != null) {
            var book = bookDAO.getBookById(targetItem.getBookId());
            if (book.getStockQuantity() < quantity) {
                response.put("status", "error");
                response.put("message", "Insufficient stock. Only " + book.getStockQuantity() + " available");
                return response;
            }
        }

        boolean success = cartDAO.updateCartItemQuantity(cartItemId, quantity);
        if (success) {
            response.put("status", "success");
            response.put("message", "Cart updated successfully");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to update cart");
        }

        return response;
    }

    public Map<String, Object> removeFromCart(int cartItemId) {
        Map<String, Object> response = new HashMap<>();
        boolean success = cartDAO.removeFromCart(cartItemId);

        if (success) {
            response.put("status", "success");
            response.put("message", "Item removed from cart");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to remove item from cart");
        }

        return response;
    }

    public Map<String, Object> getCartSummary(int userId) {
        Map<String, Object> summary = new HashMap<>();
        List<CartItem> cartItems = getCartItems(userId);

        int totalItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        double totalAmount = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();

        summary.put("totalItems", totalItems);
        summary.put("totalAmount", totalAmount);
        summary.put("itemCount", cartItems.size());

        return summary;
    }

    public boolean clearCart(int userId) {
        return cartDAO.clearCart(userId);
    }
}