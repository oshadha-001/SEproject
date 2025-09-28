package com.gtp.gtpproject.repository;

import com.gtp.gtpproject.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Find books by title containing keyword
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Find books by author containing keyword
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Find books by category
    List<Book> findByCategory(String category);

    // Find books by ISBN
    Book findByIsbn(String isbn);

    // Find low stock books
    @Query("SELECT b FROM Book b WHERE b.quantity <= b.minStockLevel ORDER BY b.quantity ASC")
    List<Book> findLowStockBooks();

    // Find critically low stock books (below 25% of min stock)
    @Query("SELECT b FROM Book b WHERE b.quantity <= (b.minStockLevel * 0.25) ORDER BY b.quantity ASC")
    List<Book> findCriticallyLowStockBooks();

    // Advanced search - search by title, author, or ISBN
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(@Param("keyword") String keyword);
}