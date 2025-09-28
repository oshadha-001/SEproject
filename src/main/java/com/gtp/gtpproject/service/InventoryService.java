package com.gtp.gtpproject.service;

import com.example.booknest.dao.BookDAO;
import com.example.booknest.dao.InventoryDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryService {
    private InventoryDAO inventoryDAO;
    private BookDAO bookDAO;

    public InventoryService() {
        this.inventoryDAO = new InventoryDAO();
        this.bookDAO = new BookDAO();
    }

    public Map<String, Object> getInventorySummary() {
        Map<String, Object> summary = inventoryDAO.getInventorySummary();

        // Add additional calculated fields
        int totalBooks = (int) summary.getOrDefault("totalBooks", 0);
        int outOfStock = (int) summary.getOrDefault("outOfStock", 0);
        double totalValue = (double) summary.getOrDefault("totalValue", 0.0);

        // Calculate inventory health
        double stockHealth = totalBooks > 0 ?
                ((double) (totalBooks - outOfStock) / totalBooks) * 100 : 0;
        summary.put("stockHealth", Math.round(stockHealth));

        return summary;
    }

    public List<Map<String, Object>> getLowStockAlerts(int threshold) {
        return inventoryDAO.getLowStockItems(threshold);
    }

    public Map<String, Object> restockBook(int bookId, int quantity) {
        Map<String, Object> response = new HashMap<>();

        if (quantity <= 0) {
            response.put("status", "error");
            response.put("message", "Restock quantity must be positive");
            return response;
        }

        var book = bookDAO.getBookById(bookId);
        if (book == null) {
            response.put("status", "error");
            response.put("message", "Book not found");
            return response;
        }

        int newStock = book.getStockQuantity() + quantity;
        boolean success = bookDAO.updateBookStock(bookId, newStock);

        if (success) {
            response.put("status", "success");
            response.put("message", "Book restocked successfully");
            response.put("newStock", newStock);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to restock book");
        }

        return response;
    }

    public Map<String, Object> getInventoryHealth() {
        Map<String, Object> health = new HashMap<>();

        var summary = getInventorySummary();
        var lowStockItems = getLowStockAlerts(5);

        int totalBooks = (int) summary.get("totalBooks");
        int lowStockCount = lowStockItems.size();
        int outOfStock = (int) summary.get("outOfStock");

        // Calculate health score (0-100)
        double healthScore = 100.0;
        if (totalBooks > 0) {
            // Penalize for out of stock items
            healthScore -= (outOfStock * 100.0 / totalBooks) * 50;
            // Penalize for low stock items
            healthScore -= (lowStockCount * 100.0 / totalBooks) * 25;
        }

        healthScore = Math.max(0, Math.min(100, healthScore));

        String healthStatus;
        if (healthScore >= 80) healthStatus = "EXCELLENT";
        else if (healthScore >= 60) healthStatus = "GOOD";
        else if (healthScore >= 40) healthStatus = "FAIR";
        else healthStatus = "POOR";

        health.put("healthScore", Math.round(healthScore));
        health.put("healthStatus", healthStatus);
        health.put("totalBooks", totalBooks);
        health.put("lowStockCount", lowStockCount);
        health.put("outOfStockCount", outOfStock);
        health.put("totalValue", summary.get("totalValue"));

        return health;
    }
}