-- BookNest Database Schema
-- Complete database schema for online bookstore system

-- Users table (extends to all user types)
CREATE TABLE users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) UNIQUE NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    first_name NVARCHAR(50) NOT NULL,
    last_name NVARCHAR(50) NOT NULL,
    phone NVARCHAR(20),
    address NVARCHAR(255),
    role NVARCHAR(20) NOT NULL CHECK (role IN ('CUSTOMER', 'ADMIN', 'INVENTORY_MANAGER', 'MARKETING_MANAGER', 'DELIVERY_STAFF')),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

-- Categories table
CREATE TABLE categories (
    category_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE()
);

-- Books table
CREATE TABLE books (
    book_id INT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(200) NOT NULL,
    author NVARCHAR(100) NOT NULL,
    isbn NVARCHAR(20) UNIQUE,
    genre NVARCHAR(50),
    category_id INT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    description NVARCHAR(1000),
    publication_date DATE,
    publisher NVARCHAR(100),
    language NVARCHAR(20) DEFAULT 'English',
    page_count INT,
    cover_image_url NVARCHAR(500),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- Suppliers table
CREATE TABLE suppliers (
    supplier_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    contact_person NVARCHAR(100),
    email NVARCHAR(100),
    phone NVARCHAR(20),
    address NVARCHAR(255),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE()
);

-- Book-Supplier relationship (many-to-many)
CREATE TABLE book_suppliers (
    book_id INT,
    supplier_id INT,
    cost_price DECIMAL(10,2),
    minimum_order_quantity INT DEFAULT 1,
    lead_time_days INT DEFAULT 7,
    PRIMARY KEY (book_id, supplier_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- Shopping Cart table
CREATE TABLE cart_items (
    cart_item_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    UNIQUE(user_id, book_id)
);

-- Orders table
CREATE TABLE orders (
    order_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    order_date DATETIME2 DEFAULT GETDATE(),
    total_amount DECIMAL(10,2) NOT NULL,
    shipping_address NVARCHAR(500) NOT NULL,
    billing_address NVARCHAR(500),
    status NVARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    payment_method NVARCHAR(50),
    payment_status NVARCHAR(20) DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    notes NVARCHAR(500),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (customer_id) REFERENCES users(user_id)
);

-- Order Items table
CREATE TABLE order_items (
    order_item_id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

-- Promotions table
CREATE TABLE promotions (
    promotion_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    discount_type NVARCHAR(20) NOT NULL CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
    discount_value DECIMAL(10,2) NOT NULL,
    minimum_order_amount DECIMAL(10,2) DEFAULT 0,
    start_date DATETIME2 NOT NULL,
    end_date DATETIME2 NOT NULL,
    is_active BIT DEFAULT 1,
    created_by INT,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Reviews table
CREATE TABLE reviews (
    review_id INT IDENTITY(1,1) PRIMARY KEY,
    book_id INT NOT NULL,
    customer_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title NVARCHAR(200),
    comment NVARCHAR(1000),
    is_verified BIT DEFAULT 0,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (customer_id) REFERENCES users(user_id),
    UNIQUE(book_id, customer_id)
);

-- Delivery Assignments table
CREATE TABLE delivery_assignments (
    assignment_id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    delivery_staff_id INT NOT NULL,
    assigned_date DATETIME2 DEFAULT GETDATE(),
    delivery_date DATE,
    status NVARCHAR(20) DEFAULT 'ASSIGNED' CHECK (status IN ('ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'FAILED')),
    delivery_notes NVARCHAR(500),
    completed_at DATETIME2,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (delivery_staff_id) REFERENCES users(user_id)
);

-- Performance Records table
CREATE TABLE performance_records (
    performance_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    task_type NVARCHAR(50) NOT NULL,
    tasks_completed INT DEFAULT 0,
    efficiency_score DECIMAL(3,2) CHECK (efficiency_score >= 0 AND efficiency_score <= 5),
    notes NVARCHAR(500),
    recorded_date DATE DEFAULT CAST(GETDATE() AS DATE),
    recorded_by INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (recorded_by) REFERENCES users(user_id)
);

-- Email Campaigns table
CREATE TABLE email_campaigns (
    campaign_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    subject NVARCHAR(200) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    target_audience NVARCHAR(50) DEFAULT 'ALL',
    scheduled_date DATETIME2,
    status NVARCHAR(20) DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'SCHEDULED', 'SENT', 'CANCELLED')),
    sent_count INT DEFAULT 0,
    opened_count INT DEFAULT 0,
    clicked_count INT DEFAULT 0,
    created_by INT,
    created_at DATETIME2 DEFAULT GETDATE(),
    sent_at DATETIME2,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- System Logs table
CREATE TABLE system_logs (
    log_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT,
    action NVARCHAR(100) NOT NULL,
    table_name NVARCHAR(50),
    record_id INT,
    old_values NVARCHAR(MAX),
    new_values NVARCHAR(MAX),
    ip_address NVARCHAR(45),
    user_agent NVARCHAR(500),
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- User Activity Logs table
CREATE TABLE user_activity_logs (
    activity_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    activity_type NVARCHAR(50) NOT NULL,
    description NVARCHAR(500),
    ip_address NVARCHAR(45),
    user_agent NVARCHAR(500),
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create indexes for better performance
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_books_genre ON books(genre);
CREATE INDEX idx_books_category ON books(category_id);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_cart_user ON cart_items(user_id);
CREATE INDEX idx_reviews_book ON reviews(book_id);
CREATE INDEX idx_delivery_staff ON delivery_assignments(delivery_staff_id);
CREATE INDEX idx_system_logs_user ON system_logs(user_id);
CREATE INDEX idx_system_logs_date ON system_logs(created_at);
CREATE INDEX idx_activity_logs_user ON user_activity_logs(user_id);
CREATE INDEX idx_activity_logs_date ON user_activity_logs(created_at);

-- Insert default categories
INSERT INTO categories (name, description) VALUES 
('Fiction', 'Novels, short stories, and other fictional works'),
('Non-Fiction', 'Biographies, history, science, and other factual works'),
('Mystery & Thriller', 'Crime, suspense, and thriller novels'),
('Romance', 'Romantic fiction and love stories'),
('Science Fiction', 'Futuristic and speculative fiction'),
('Fantasy', 'Fantasy and magical fiction'),
('Biography', 'Life stories and memoirs'),
('History', 'Historical accounts and analysis'),
('Self-Help', 'Personal development and improvement books'),
('Business', 'Business and management literature');

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password_hash, first_name, last_name, role) VALUES 
('admin', 'admin@booknest.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'System', 'Administrator', 'ADMIN');

-- Insert sample books
INSERT INTO books (title, author, isbn, genre, category_id, price, stock_quantity, description) VALUES 
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Fiction', 1, 12.99, 50, 'A classic American novel set in the Jazz Age'),
('To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'Fiction', 1, 14.99, 30, 'A gripping tale of racial injustice and childhood innocence'),
('1984', 'George Orwell', '9780451524935', 'Science Fiction', 5, 13.99, 40, 'A dystopian social science fiction novel'),
('Pride and Prejudice', 'Jane Austen', '9780141439518', 'Romance', 4, 11.99, 25, 'A romantic novel of manners'),
('The Catcher in the Rye', 'J.D. Salinger', '9780316769174', 'Fiction', 1, 15.99, 35, 'A coming-of-age story');

-- Insert sample suppliers
INSERT INTO suppliers (name, contact_person, email, phone, address) VALUES 
('Book Distributors Inc', 'John Smith', 'john@bookdist.com', '555-0101', '123 Book Street, City, State'),
('Literary Supply Co', 'Jane Doe', 'jane@literarysupply.com', '555-0102', '456 Literature Ave, City, State'),
('Publishing Partners', 'Bob Johnson', 'bob@publishingpartners.com', '555-0103', '789 Print Lane, City, State');

-- Link books with suppliers
INSERT INTO book_suppliers (book_id, supplier_id, cost_price, minimum_order_quantity, lead_time_days) VALUES 
(1, 1, 8.50, 10, 5),
(2, 1, 9.50, 10, 5),
(3, 2, 8.00, 15, 7),
(4, 2, 7.50, 10, 5),
(5, 3, 10.00, 12, 6);
