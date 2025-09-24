package com.gtp.gtpproject.controller;

import com.gtp.gtpproject.model.Book;
import com.gtp.gtpproject.repository.BookRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepo;

    public BookController(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    // Show add form + all books
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("books", bookRepo.findAll());
        return "add-book";
    }

    // Save book and reload form with table
    @PostMapping("/add")
    public String saveBook(@ModelAttribute("book") Book book, Model model) {
        bookRepo.save(book); // Save to DB
        model.addAttribute("book", new Book()); // Clear form
        model.addAttribute("books", bookRepo.findAll()); // Reload updated book list
        return "add-book"; // Stay on same page
    }

    // Delete book
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, Model model) {
        bookRepo.deleteById(id);
        return "redirect:/books/add";
    }

    // Show list of books on main page
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookRepo.findAll());
        return "books"; // loads books.html
    }
    @Controller
    public class HomeController {

        @GetMapping("/")
        public String home() {
            return "redirect:/books"; // redirect root to books page
        }
    }

}
