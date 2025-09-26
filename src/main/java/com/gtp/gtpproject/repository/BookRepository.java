package com.gtp.gtpproject.repository;


import com.gtp.gtpproject.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}

