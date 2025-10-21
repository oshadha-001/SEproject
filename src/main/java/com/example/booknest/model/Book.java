package com.example.booknest.model;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private double price;
    private int stockQuantity;
    private String description;
    private String publisher;
    private String publishedDate;
    private String imageUrl;
    private boolean isActive;

    public Book() {}
    public Book(String title, String author, String isbn, String genre, double price,
                int stockQuantity, String description, String publisher) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.publisher = publisher;
        this.isActive = true;
    }

    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getPublishedDate() { return publishedDate; }
    public void setPublishedDate(String publishedDate) { this.publishedDate = publishedDate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}