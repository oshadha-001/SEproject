package com.gtp.gtpproject.controller;

import com.gtp.gtpproject.model.Book;
import com.gtp.gtpproject.repository.BookRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/inventory")
public class BookController {

    private final BookRepository bookRepo;

    public BookController(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    // Enhanced dashboard with categories
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Book> allBooks = bookRepo.findAll();
        List<Book> lowStockBooks = allBooks.stream()
                .filter(Book::isLowStock)
                .collect(Collectors.toList());

        // Get unique categories
        Set<String> categories = allBooks.stream()
                .map(Book::getCategory)
                .filter(Objects::nonNull)
                .filter(cat -> !cat.trim().isEmpty())
                .collect(Collectors.toSet());

        model.addAttribute("lowStockBooks", lowStockBooks);
        model.addAttribute("totalBooks", allBooks.size());
        model.addAttribute("books", allBooks);
        model.addAttribute("categories", categories);

        return "inventory-dashboard";
    }

    // Show add book form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("books", bookRepo.findAll());
        return "add-book";
    }

    // Save new book
    @PostMapping("/add")
    public String saveBook(@ModelAttribute Book book, RedirectAttributes redirectAttributes) {
        try {
            // Set default values if needed
            if (book.getMinStockLevel() == 0) {
                book.setMinStockLevel(10);
            }

            bookRepo.save(book);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Book '" + book.getTitle() + "' added successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error adding book: " + e.getMessage());
        }
        return "redirect:/inventory/add";
    }

    // Show edit book form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + id));
        model.addAttribute("book", book);
        return "edit-book";
    }

    // Update book
    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book,
                             RedirectAttributes redirectAttributes) {
        try {
            book.setId(id);
            bookRepo.save(book);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Book '" + book.getTitle() + "' updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error updating book: " + e.getMessage());
        }
        return "redirect:/inventory/dashboard";
    }

    // Update stock quantity only
    @GetMapping("/update-stock/{id}")
    public String showUpdateStockForm(@PathVariable Long id, Model model) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + id));
        model.addAttribute("book", book);
        return "update-stock";
    }

    @PostMapping("/update-stock/{id}")
    public String updateStock(@PathVariable Long id, @RequestParam int quantity,
                              RedirectAttributes redirectAttributes) {
        try {
            Book book = bookRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + id));

            int oldQuantity = book.getQuantity();
            book.setQuantity(quantity);
            bookRepo.save(book);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Stock updated for '" + book.getTitle() + "'. Old: " + oldQuantity + ", New: " + quantity);

            if (book.isLowStock()) {
                redirectAttributes.addFlashAttribute("warningMessage",
                        "Low stock alert! Please reorder '" + book.getTitle() + "'");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error updating stock: " + e.getMessage());
        }
        return "redirect:/inventory/dashboard";
    }

    // Delete book
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Book book = bookRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + id));
            bookRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Book '" + book.getTitle() + "' deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting book: " + e.getMessage());
        }
        return "redirect:/inventory/add";
    }

    // Search books
    @GetMapping("/search")
    public String searchBooks(@RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "category", required = false) String category,
                              Model model) {

        List<Book> searchResults;
        List<Book> allBooks = bookRepo.findAll();

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Use the advanced search method that searches title, author, and ISBN
            searchResults = bookRepo.searchBooks(keyword.trim());
        } else if (category != null && !category.trim().isEmpty()) {
            // Search by category
            searchResults = bookRepo.findByCategory(category.trim());
        } else {
            // If no search criteria, show all books
            searchResults = allBooks;
        }

        // Get low stock books for the dashboard stats
        List<Book> lowStockBooks = allBooks.stream()
                .filter(Book::isLowStock)
                .collect(Collectors.toList());

        // Get unique categories
        Set<String> categories = allBooks.stream()
                .map(Book::getCategory)
                .filter(Objects::nonNull)
                .filter(cat -> !cat.trim().isEmpty())
                .collect(Collectors.toSet());

        model.addAttribute("books", searchResults);
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("searchCategory", category);
        model.addAttribute("lowStockBooks", lowStockBooks);
        model.addAttribute("totalBooks", allBooks.size());
        model.addAttribute("categories", categories);
        model.addAttribute("searchResultsCount", searchResults.size());

        return "inventory-dashboard";
    }

    // Clear search and show all books
    @GetMapping("/clear-search")
    public String clearSearch(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", "Search cleared. Showing all books.");
        return "redirect:/inventory/dashboard";
    }

    // Low stock report - Main page
    @GetMapping("/low-stock")
    public String lowStockReport(Model model) {
        List<Book> lowStockBooks = bookRepo.findLowStockBooks();
        List<Book> criticallyLowStockBooks = bookRepo.findCriticallyLowStockBooks();

        int totalLowStock = lowStockBooks.size();
        int totalCriticallyLow = criticallyLowStockBooks.size();
        double totalReorderCost = calculateTotalReorderCost(lowStockBooks);

        model.addAttribute("lowStockBooks", lowStockBooks);
        model.addAttribute("criticallyLowStockBooks", criticallyLowStockBooks);
        model.addAttribute("totalLowStock", totalLowStock);
        model.addAttribute("totalCriticallyLow", totalCriticallyLow);
        model.addAttribute("totalReorderCost", totalReorderCost);
        model.addAttribute("reportTitle", "Low Stock Alert Report");

        return "low-stock-report";
    }

    // Critical stock report only
    @GetMapping("/critical-stock")
    public String criticalStockReport(Model model) {
        List<Book> criticallyLowStockBooks = bookRepo.findCriticallyLowStockBooks();

        model.addAttribute("lowStockBooks", criticallyLowStockBooks);
        model.addAttribute("totalLowStock", criticallyLowStockBooks.size());
        model.addAttribute("reportTitle", "Critical Stock Alert Report");

        return "low-stock-report";
    }

    // Export low stock report (simple HTML view for printing)
    @GetMapping("/low-stock/export")
    public String exportLowStockReport(Model model) {
        List<Book> lowStockBooks = bookRepo.findLowStockBooks();
        List<Book> criticallyLowStockBooks = bookRepo.findCriticallyLowStockBooks();

        int totalLowStock = lowStockBooks.size();
        int totalCriticallyLow = criticallyLowStockBooks.size();
        double totalReorderCost = calculateTotalReorderCost(lowStockBooks);

        model.addAttribute("lowStockBooks", lowStockBooks);
        model.addAttribute("criticallyLowStockBooks", criticallyLowStockBooks);
        model.addAttribute("totalLowStock", totalLowStock);
        model.addAttribute("totalCriticallyLow", totalCriticallyLow);
        model.addAttribute("totalReorderCost", totalReorderCost);
        model.addAttribute("reportTitle", "Low Stock Report - Export");
        model.addAttribute("isExport", true);

        return "low-stock-export";
    }

    // Helper method to calculate total reorder cost
    private double calculateTotalReorderCost(List<Book> lowStockBooks) {
        return lowStockBooks.stream()
                .mapToDouble(book -> {
                    int reorderQuantity = Math.max(book.getMinStockLevel() * 2 - book.getQuantity(), 10);
                    return book.getPrice() * reorderQuantity;
                })
                .sum();
    }
}