package com.example.booknest;

import com.example.booknest.controller.*;
import com.example.booknest.util.DatabaseConnection;
import com.google.gson.Gson;
import spark.Spark;
import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.*;

import static spark.Spark.*;

public class Main {
    private static Connection connection;
    private static Gson gson = new Gson();

    public static class User {
        private int userId;
        private String firstName;
        private String lastName;
        private String username;
        private String email;
        private String phone;
        private String role;
        private String password;

        public User() {}

        public User(int userId, String firstName, String lastName, String username,
                    String email, String phone, String role) {
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.role = role;
        }

        // Getters and setters
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static void main(String[] args) {
        // Configure Spark
        staticFiles.location("/public");
        externalStaticFileLocation("src/main/resources/public");
        port(4567);

        // Configure session with timeout
        before((request, response) -> {
            // This ensures sessions are created when needed
        });

        // Alternative approach using static method
        Spark.staticFiles.expireTime(30 * 60); // 30 minutes for static files
        enableCORS();
        initializeDatabase();
        setupRoutes();

        System.out.println("Online Book Nest System started on http://localhost:4567");

        // SECOND OUTPUT ADDED HERE
        System.out.println("Server is running successfully and ready for connections!");
    }

    private static void enableCORS() {
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
            response.header("Access-Control-Allow-Credentials", "true");
        });
    }

    private static void initializeDatabase() {
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=bookNest;encrypt=true;trustServerCertificate=true";
            String username = "sa";
            String password = "789";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully to: bookNest");

            // Initialize database schema
            DatabaseConnection.initializeDatabase();

            // Test database connection
            testDatabaseConnection();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testDatabaseConnection() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            if (rs.next()) {
                System.out.println("✅ Database connection test successful");
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
        }
    }

    private static void createTables() {
        try {
            Statement stmt = connection.createStatement();

            // Create users table if not exists
            String usersTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='users' AND xtype='U') " +
                    "CREATE TABLE users (" +
                    "user_id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "first_name VARCHAR(100) NOT NULL, " +
                    "last_name VARCHAR(100) NOT NULL, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL, " +
                    "phone VARCHAR(20), " +
                    "role VARCHAR(20) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "is_active BIT DEFAULT 1, " +
                    "created_at DATETIME DEFAULT GETDATE())";
            stmt.execute(usersTable);

            // Create role-specific tables
            createRoleSpecificTables(stmt);

            // Create books table if not exists
            String booksTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='books' AND xtype='U') " +
                    "CREATE TABLE books (" +
                    "book_id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "author VARCHAR(255) NOT NULL, " +
                    "isbn VARCHAR(20), " +
                    "genre VARCHAR(100), " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "stock_quantity INT DEFAULT 0, " +
                    "description TEXT, " +
                    "publisher VARCHAR(255), " +
                    "published_date VARCHAR(50), " +
                    "image_url VARCHAR(500), " +
                    "is_active BIT DEFAULT 1, " +
                    "created_at DATETIME DEFAULT GETDATE())";
            stmt.execute(booksTable);

            // Create orders table if not exists
            String ordersTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='orders' AND xtype='U') " +
                    "CREATE TABLE orders (" +
                    "order_id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "customer_id INT NOT NULL, " +
                    "order_date DATETIME DEFAULT GETDATE(), " +
                    "total_amount DECIMAL(10,2) NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'PENDING', " +
                    "shipping_address TEXT NOT NULL, " +
                    "payment_method VARCHAR(50) NOT NULL, " +
                    "promotion_id INT, " +
                    "FOREIGN KEY (customer_id) REFERENCES users(user_id))";
            stmt.execute(ordersTable);

            // Create order_items table if not exists
            String orderItemsTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='order_items' AND xtype='U') " +
                    "CREATE TABLE order_items (" +
                    "order_item_id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "order_id INT NOT NULL, " +
                    "book_id INT NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "unit_price DECIMAL(10,2) NOT NULL, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(order_id), " +
                    "FOREIGN KEY (book_id) REFERENCES books(book_id))";
            stmt.execute(orderItemsTable);

            // Create cart_items table if not exists
            String cartItemsTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='cart_items' AND xtype='U') " +
                    "CREATE TABLE cart_items (" +
                    "cart_item_id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "book_id INT NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "added_date DATETIME DEFAULT GETDATE(), " +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                    "FOREIGN KEY (book_id) REFERENCES books(book_id))";
            stmt.execute(cartItemsTable);

            // Create promotions table if not exists
            String promotionsTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='promotions' AND xtype='U') " +
                    "CREATE TABLE promotions (" +
                    "promotion_id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "promotion_name VARCHAR(255) NOT NULL, " +
                    "description TEXT, " +
                    "discount_percentage DECIMAL(5,2) NOT NULL, " +
                    "start_date DATETIME NOT NULL, " +
                    "end_date DATETIME NOT NULL, " +
                    "is_active BIT DEFAULT 1, " +
                    "created_at DATETIME DEFAULT GETDATE())";
            stmt.execute(promotionsTable);

            // Create categories table if not exists
            String categoriesTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='categories' AND xtype='U') " +
                    "CREATE TABLE categories (" +
                    "category_id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "category_name VARCHAR(100) NOT NULL, " +
                    "description TEXT, " +
                    "is_active BIT DEFAULT 1, " +
                    "created_at DATETIME DEFAULT GETDATE())";
            stmt.execute(categoriesTable);

            // Insert sample books if empty
            String checkBooks = "SELECT COUNT(*) as count FROM books";
            ResultSet rs = stmt.executeQuery(checkBooks);
            if (rs.next() && rs.getInt("count") == 0) {
                String insertBooks = "INSERT INTO books (title, author, isbn, genre, price, stock_quantity, description, publisher, image_url) VALUES " +
                        "('The Great Gatsby', 'F. Scott Fitzgerald', '978-0-7432-7356-5', 'Fiction', 12.99, 50, 'A classic American novel set in the Jazz Age', 'Scribner', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'), " +
                        "('To Kill a Mockingbird', 'Harper Lee', '978-0-06-112008-4', 'Fiction', 14.99, 30, 'A gripping tale of racial injustice and childhood innocence', 'J.B. Lippincott & Co.', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'), " +
                        "('1984', 'George Orwell', '978-0-452-28423-4', 'Dystopian', 10.99, 25, 'A dystopian social science fiction novel', 'Secker & Warburg', 'https://images.unsplash.com/photo-1629992101753-56d196c8aabb?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'), " +
                        "('Pride and Prejudice', 'Jane Austen', '978-0-14-143951-8', 'Romance', 9.99, 40, 'A romantic novel of manners', 'T. Egerton, Whitehall', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'), " +
                        "('The Hobbit', 'J.R.R. Tolkien', '978-0-547-92822-7', 'Fantasy', 15.99, 35, 'A fantasy novel about a hobbit''s unexpected journey', 'George Allen & Unwin', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'), " +
                        "('The Silent Ocean', 'Amanda Richardson', '978-1-2345-6789-0', 'Mystery', 13.99, 20, 'A thrilling mystery set on the high seas', 'Ocean Press', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'), " +
                        "('Echoes of Time', 'Robert Chen', '978-1-2345-6789-1', 'Science Fiction', 16.99, 15, 'A time-travel adventure through history', 'Future Books', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'), " +
                        "('Whispers in the Dark', 'Sarah Johnson', '978-1-2345-6789-2', 'Horror', 11.99, 25, 'A spine-chilling horror novel', 'Dark Tales Publishing', 'https://images.unsplash.com/photo-1629992101753-56d196c8aabb?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'), " +
                        "('The Last Ember', 'Michael Torres', '978-1-2345-6789-3', 'Adventure', 14.99, 30, 'An epic adventure in a post-apocalyptic world', 'Adventure Press', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80')";
                stmt.execute(insertBooks);
            }

            // Insert sample categories if empty
            String checkCategories = "SELECT COUNT(*) as count FROM categories";
            ResultSet rs2 = stmt.executeQuery(checkCategories);
            if (rs2.next() && rs2.getInt("count") == 0) {
                String insertCategories = "INSERT INTO categories (category_name, description) VALUES " +
                        "('Fiction', 'Literary works of imagination'), " +
                        "('Mystery', 'Stories involving crime and investigation'), " +
                        "('Science Fiction', 'Speculative fiction with scientific elements'), " +
                        "('Romance', 'Stories focused on romantic relationships'), " +
                        "('Fantasy', 'Stories with magical or supernatural elements'), " +
                        "('Horror', 'Stories intended to frighten or unsettle'), " +
                        "('Adventure', 'Stories of exciting experiences and journeys'), " +
                        "('Dystopian', 'Stories set in undesirable or frightening societies')";
                stmt.execute(insertCategories);
            }

            // Insert sample promotions if empty
            String checkPromotions = "SELECT COUNT(*) as count FROM promotions";
            ResultSet rs3 = stmt.executeQuery(checkPromotions);
            if (rs3.next() && rs3.getInt("count") == 0) {
                String insertPromotions = "INSERT INTO promotions (promotion_name, description, discount_percentage, start_date, end_date) VALUES " +
                        "('Summer Reading Sale', 'Get 20% off on all fiction books', 20.00, '2024-06-01', '2024-08-31'), " +
                        "('New Arrivals Discount', '15% off on newly added books', 15.00, '2024-01-01', '2024-12-31'), " +
                        "('Student Special', '25% off for students with valid ID', 25.00, '2024-09-01', '2024-12-31')";
                stmt.execute(insertPromotions);
            }

            // Create additional management tables
            createManagementTables(stmt);

            System.out.println("Database tables verified successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createRoleSpecificTables(Statement stmt) throws SQLException {
        // Create customers table
        String customersTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='customers' AND xtype='U') " +
                "CREATE TABLE customers (" +
                "customer_id INT IDENTITY(1,1) PRIMARY KEY, " +
                "user_id INT FOREIGN KEY REFERENCES users(user_id), " +
                "membership_level VARCHAR(50) DEFAULT 'STANDARD', " +
                "created_at DATETIME DEFAULT GETDATE())";
        stmt.execute(customersTable);

        // Create inventory_managers table
        String inventoryTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='inventory_managers' AND xtype='U') " +
                "CREATE TABLE inventory_managers (" +
                "manager_id INT IDENTITY(1,1) PRIMARY KEY, " +
                "user_id INT FOREIGN KEY REFERENCES users(user_id), " +
                "department VARCHAR(100) DEFAULT 'Inventory', " +
                "created_at DATETIME DEFAULT GETDATE())";
        stmt.execute(inventoryTable);

        // Create marketing_managers table
        String marketingTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='marketing_managers' AND xtype='U') " +
                "CREATE TABLE marketing_managers (" +
                "manager_id INT IDENTITY(1,1) PRIMARY KEY, " +
                "user_id INT FOREIGN KEY REFERENCES users(user_id), " +
                "department VARCHAR(100) DEFAULT 'Marketing', " +
                "created_at DATETIME DEFAULT GETDATE())";
        stmt.execute(marketingTable);

        // Create delivery_staff table
        String deliveryTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='delivery_staff' AND xtype='U') " +
                "CREATE TABLE delivery_staff (" +
                "staff_id INT IDENTITY(1,1) PRIMARY KEY, " +
                "user_id INT FOREIGN KEY REFERENCES users(user_id), " +
                "vehicle_type VARCHAR(50) DEFAULT 'BIKE', " +
                "created_at DATETIME DEFAULT GETDATE())";
        stmt.execute(deliveryTable);

        // Create admins table
        String adminTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='admins' AND xtype='U') " +
                "CREATE TABLE admins (" +
                "admin_id INT IDENTITY(1,1) PRIMARY KEY, " +
                "user_id INT FOREIGN KEY REFERENCES users(user_id), " +
                "admin_level VARCHAR(50) DEFAULT 'SUPER_ADMIN', " +
                "created_at DATETIME DEFAULT GETDATE())";
        stmt.execute(adminTable);
    }

    private static void createManagementTables(Statement stmt) throws SQLException {
        // Create reviews table
        String reviewsTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='reviews' AND xtype='U') " +
                "CREATE TABLE reviews (" +
                "review_id INT IDENTITY(1,1) PRIMARY KEY, " +
                "book_id INT NOT NULL, " +
                "user_id INT NOT NULL, " +
                "rating INT CHECK (rating >= 1 AND rating <= 5), " +
                "comment TEXT, " +
                "created_at DATETIME DEFAULT GETDATE(), " +
                "is_active BIT DEFAULT 1, " +
                "FOREIGN KEY (book_id) REFERENCES books(book_id), " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))";
        stmt.execute(reviewsTable);

        // Create suppliers table
        String suppliersTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='suppliers' AND xtype='U') " +
                "CREATE TABLE suppliers (" +
                "supplier_id INT IDENTITY(1,1) PRIMARY KEY, " +
                "supplier_name VARCHAR(255) NOT NULL, " +
                "contact_person VARCHAR(255), " +
                "email VARCHAR(255), " +
                "phone VARCHAR(20), " +
                "address TEXT, " +
                "created_at DATETIME DEFAULT GETDATE(), " +
                "is_active BIT DEFAULT 1)";
        stmt.execute(suppliersTable);

        // Create performance_records table
        String performanceTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='performance_records' AND xtype='U') " +
                "CREATE TABLE performance_records (" +
                "record_id INT IDENTITY(1,1) PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "performance_date DATE NOT NULL, " +
                "tasks_completed INT DEFAULT 0, " +
                "efficiency_score DECIMAL(5,2), " +
                "notes TEXT, " +
                "created_at DATETIME DEFAULT GETDATE(), " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))";
        stmt.execute(performanceTable);

        // Create delivery_assignments table
        String deliveryAssignmentsTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='delivery_assignments' AND xtype='U') " +
                "CREATE TABLE delivery_assignments (" +
                "assignment_id INT IDENTITY(1,1) PRIMARY KEY, " +
                "order_id INT NOT NULL, " +
                "delivery_staff_id INT NOT NULL, " +
                "assigned_date DATETIME DEFAULT GETDATE(), " +
                "delivery_date DATE, " +
                "status VARCHAR(50) DEFAULT 'ASSIGNED', " +
                "delivery_notes TEXT, " +
                "completed_at DATETIME, " +
                "FOREIGN KEY (order_id) REFERENCES orders(order_id), " +
                "FOREIGN KEY (delivery_staff_id) REFERENCES users(user_id))";
        stmt.execute(deliveryAssignmentsTable);

        // Create system_logs table
        String systemLogsTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='system_logs' AND xtype='U') " +
                "CREATE TABLE system_logs (" +
                "log_id INT IDENTITY(1,1) PRIMARY KEY, " +
                "user_id INT, " +
                "action VARCHAR(255) NOT NULL, " +
                "table_name VARCHAR(100), " +
                "record_id INT, " +
                "old_values TEXT, " +
                "new_values TEXT, " +
                "ip_address VARCHAR(45), " +
                "user_agent TEXT, " +
                "created_at DATETIME DEFAULT GETDATE(), " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))";
        stmt.execute(systemLogsTable);

        // Create email_campaigns table
        String emailCampaignsTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='email_campaigns' AND xtype='U') " +
                "CREATE TABLE email_campaigns (" +
                "campaign_id INT IDENTITY(1,1) PRIMARY KEY, " +
                "campaign_name VARCHAR(255) NOT NULL, " +
                "subject VARCHAR(255) NOT NULL, " +
                "content TEXT NOT NULL, " +
                "target_audience VARCHAR(100), " +
                "sent_count INT DEFAULT 0, " +
                "opened_count INT DEFAULT 0, " +
                "clicked_count INT DEFAULT 0, " +
                "created_at DATETIME DEFAULT GETDATE(), " +
                "sent_at DATETIME, " +
                "is_active BIT DEFAULT 1)";
        stmt.execute(emailCampaignsTable);

        // Create book_suppliers table (many-to-many relationship)
        String bookSuppliersTable = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='book_suppliers' AND xtype='U') " +
                "CREATE TABLE book_suppliers (" +
                "book_id INT NOT NULL, " +
                "supplier_id INT NOT NULL, " +
                "supplier_price DECIMAL(10,2), " +
                "lead_time_days INT, " +
                "is_primary BIT DEFAULT 0, " +
                "created_at DATETIME DEFAULT GETDATE(), " +
                "PRIMARY KEY (book_id, supplier_id), " +
                "FOREIGN KEY (book_id) REFERENCES books(book_id), " +
                "FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id))";
        stmt.execute(bookSuppliersTable);
    }

    private static void createRoleSpecificRecord(int userId, String role, Connection conn) throws SQLException {
        String sql = "";
        switch (role.toUpperCase()) {
            case "CUSTOMER":
                sql = "INSERT INTO customers (user_id, membership_level) VALUES (?, 'STANDARD')";
                break;
            case "INVENTORY_MANAGER":
                sql = "INSERT INTO inventory_managers (user_id, department) VALUES (?, 'Inventory')";
                break;
            case "ADMIN":
                sql = "INSERT INTO admins (user_id, admin_level) VALUES (?, 'SUPER_ADMIN')";
                break;
            case "MARKETING_MANAGER":
                sql = "INSERT INTO marketing_managers (user_id, department) VALUES (?, 'Marketing')";
                break;
            case "DELIVERY_STAFF":
                sql = "INSERT INTO delivery_staff (user_id, vehicle_type) VALUES (?, 'BIKE')";
                break;
        }

        if (!sql.isEmpty()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    private static void setupRoutes() {
        // Generic login endpoint that routes based on role
        post("/api/login", (req, res) -> {
            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            String username = body.get("username");
            String password = body.get("password");
            String role = body.get("role");

            System.out.println("Login attempt - Username: " + username + ", Role: " + role);

            try {
                String sql;
                PreparedStatement pstmt;

                if (role != null && !role.isEmpty() && !"CUSTOMER".equals(role)) {
                    // Staff login
                    sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ? AND is_active = 1";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    pstmt.setString(3, role);
                } else {
                    // Customer login (default)
                    sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = 'CUSTOMER' AND is_active = 1";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                }

                ResultSet rs = pstmt.executeQuery();
                Map<String, Object> response = new HashMap<>();

                if (rs.next()) {
                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("role")
                    );

                    req.session(true);
                    req.session().attribute("user", user);

                    response.put("status", "success");
                    response.put("message", "Login successful");
                    response.put("user", user);
                    System.out.println("Login successful for user: " + username);
                } else {
                    response.put("status", "error");
                    response.put("message", "Invalid username or password");
                    System.out.println("Login failed for user: " + username);
                }

                res.type("application/json");
                return gson.toJson(response);

            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Database error in login: " + e.getMessage());
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Database error: " + e.getMessage());
                res.type("application/json");
                return gson.toJson(response);
            }
        });

        // Generic register endpoint that routes based on data
        post("/api/register", (req, res) -> {
            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            String role = body.get("role");

            System.out.println("Registration attempt - Username: " + body.get("username") + ", Role: " + role);

            if (role == null || "CUSTOMER".equals(role)) {
                // Customer registration
                handleCustomerRegistration(req, res, body);
            } else {
                // Staff registration
                handleStaffRegistration(req, res, body);
            }
            return res.body();
        });

        // Customer login (specific endpoint)
        post("/api/customer/login", (req, res) -> {
            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            String username = body.get("username");
            String password = body.get("password");

            System.out.println("Customer login attempt - Username: " + username);

            try {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = 'CUSTOMER' AND is_active = 1";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, password);

                ResultSet rs = pstmt.executeQuery();
                Map<String, Object> response = new HashMap<>();

                if (rs.next()) {
                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("role")
                    );

                    req.session(true);
                    req.session().attribute("user", user);

                    response.put("status", "success");
                    response.put("message", "Login successful");
                    response.put("user", user);
                    System.out.println("Customer login successful for user: " + username);
                } else {
                    response.put("status", "error");
                    response.put("message", "Invalid username or password");
                    System.out.println("Customer login failed for user: " + username);
                }

                res.type("application/json");
                return gson.toJson(response);

            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Database error in customer login: " + e.getMessage());
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Database error: " + e.getMessage());
                res.type("application/json");
                return gson.toJson(response);
            }
        });

        // Staff login (specific endpoint)
        post("/api/staff/login", (req, res) -> {
            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            String username = body.get("username");
            String password = body.get("password");
            String role = body.get("role");

            System.out.println("Staff login attempt - Username: " + username + ", Role: " + role);

            try {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ? AND role != 'CUSTOMER' AND is_active = 1";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, role);

                ResultSet rs = pstmt.executeQuery();
                Map<String, Object> response = new HashMap<>();

                if (rs.next()) {
                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("role")
                    );

                    req.session(true);
                    req.session().attribute("user", user);

                    response.put("status", "success");
                    response.put("message", "Login successful");
                    response.put("user", user);
                    System.out.println("Staff login successful for user: " + username);
                } else {
                    response.put("status", "error");
                    response.put("message", "Invalid credentials for selected role");
                    System.out.println("Staff login failed for user: " + username);
                }

                res.type("application/json");
                return gson.toJson(response);

            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Database error in staff login: " + e.getMessage());
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Database error: " + e.getMessage());
                res.type("application/json");
                return gson.toJson(response);
            }
        });

        // Customer registration (specific endpoint)
        post("/api/customer/register", (req, res) -> {
            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            handleCustomerRegistration(req, res, body);
            return res.body();
        });

        // Staff registration (specific endpoint)
        post("/api/staff/register", (req, res) -> {
            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            handleStaffRegistration(req, res, body);
            return res.body();
        });

        // Customer dashboard
        get("/customer-dashboard", (req, res) -> {
            User user = req.session().attribute("user");
            if (user == null || !"CUSTOMER".equals(user.getRole())) {
                res.redirect("/customer-login.html");
                return null;
            }
            res.redirect("/customer-dashboard.html");
            return null;
        });

        // Check authentication
        get("/api/check-auth", (req, res) -> {
            User user = req.session().attribute("user");
            Map<String, Object> response = new HashMap<>();

            if (user != null) {
                response.put("authenticated", true);
                response.put("user", user);
            } else {
                response.put("authenticated", false);
            }

            res.type("application/json");
            return gson.toJson(response);
        });

        // Dashboard route - redirects based on role
        get("/dashboard", (req, res) -> {
            User user = req.session().attribute("user");
            if (user == null) {
                res.redirect("/staff-login.html");
                return null;
            }

            String role = user.getRole();
            switch (role) {
                case "CUSTOMER":
                    res.redirect("/customer-dashboard.html");
                    break;
                case "ADMIN":
                    res.redirect("/admin-dashboard.html");
                    break;
                case "INVENTORY_MANAGER":
                    res.redirect("/inventoryManagerIndex.html");
                    break;
                case "MARKETING_MANAGER":
                    res.redirect("/marketing-dashboard.html");
                    break;
                case "DELIVERY_STAFF":
                    res.redirect("/delivery-dashboard.html");
                    break;
                case "BOOKSTORE_MANAGER":
                    res.redirect("/admin-dashboard.html");
                    break;
                default:
                    res.redirect("/customer-login.html");
            }
            return null;
        });

        // Get all books
        get("/api/books", (req, res) -> {
            try {
                List<Map<String, Object>> books = new ArrayList<>();
                String sql = "SELECT * FROM books";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    Map<String, Object> book = new HashMap<>();
                    book.put("bookId", rs.getInt("book_id"));
                    book.put("title", rs.getString("title"));
                    book.put("author", rs.getString("author"));
                    book.put("genre", rs.getString("genre"));
                    book.put("price", rs.getDouble("price"));
                    book.put("stockQuantity", rs.getInt("stock_quantity"));
                    books.add(book);
                }

                res.type("application/json");
                return gson.toJson(books);

            } catch (SQLException e) {
                e.printStackTrace();
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Failed to fetch books");
                res.type("application/json");
                return gson.toJson(response);
            }
        });

        // Get all users (admin only)
        get("/api/users", (req, res) -> {
            User user = req.session().attribute("user");
            if (user == null || !"ADMIN".equals(user.getRole())) {
                res.status(403);
                return gson.toJson(Map.of("status", "error", "message", "Access denied"));
            }

            try {
                List<Map<String, Object>> users = new ArrayList<>();
                String sql = "SELECT user_id, first_name, last_name, username, email, phone, role FROM users WHERE is_active = 1";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userId", rs.getInt("user_id"));
                    userMap.put("firstName", rs.getString("first_name"));
                    userMap.put("lastName", rs.getString("last_name"));
                    userMap.put("username", rs.getString("username"));
                    userMap.put("email", rs.getString("email"));
                    userMap.put("phone", rs.getString("phone"));
                    userMap.put("role", rs.getString("role"));
                    users.add(userMap);
                }

                res.type("application/json");
                return gson.toJson(users);

            } catch (SQLException e) {
                e.printStackTrace();
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Failed to fetch users");
                res.type("application/json");
                return gson.toJson(response);
            }
        });

        // Add user (admin only)
        post("/api/users", (req, res) -> {
            User adminUser = req.session().attribute("user");
            if (adminUser == null || !"ADMIN".equals(adminUser.getRole())) {
                res.status(403);
                return gson.toJson(Map.of("status", "error", "message", "Access denied"));
            }

            Map<String, String> body = gson.fromJson(req.body(), Map.class);

            try {
                String sql = "INSERT INTO users (first_name, last_name, username, email, phone, role, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, body.get("firstName"));
                pstmt.setString(2, body.get("lastName"));
                pstmt.setString(3, body.get("username"));
                pstmt.setString(4, body.get("email"));
                pstmt.setString(5, body.get("phone"));
                pstmt.setString(6, body.get("role"));
                pstmt.setString(7, body.get("password"));

                int affectedRows = pstmt.executeUpdate();
                Map<String, Object> response = new HashMap<>();

                if (affectedRows > 0) {
                    response.put("status", "success");
                    response.put("message", "User added successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to add user");
                }

                res.type("application/json");
                return gson.toJson(response);

            } catch (SQLException e) {
                e.printStackTrace();
                Map<String, Object> response = new HashMap<>();
                if (e.getMessage().contains("unique constraint")) {
                    response.put("status", "error");
                    response.put("message", "Username or email already exists");
                } else {
                    response.put("status", "error");
                    response.put("message", "Database error: " + e.getMessage());
                }
                res.type("application/json");
                return gson.toJson(response);
            }
        });

        // Update user (admin only)
        put("/api/users/:id", (req, res) -> {
            User adminUser = req.session().attribute("user");
            if (adminUser == null || !"ADMIN".equals(adminUser.getRole())) {
                res.status(403);
                return gson.toJson(Map.of("status", "error", "message", "Access denied"));
            }

            int userId = Integer.parseInt(req.params(":id"));
            Map<String, Object> body = gson.fromJson(req.body(), Map.class);

            try {
                StringBuilder sql = new StringBuilder("UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ?, role = ?");
                List<Object> params = new ArrayList<>();

                params.add(body.get("firstName"));
                params.add(body.get("lastName"));
                params.add(body.get("email"));
                params.add(body.get("phone"));
                params.add(body.get("role"));

                // Add password update if provided
                if (body.containsKey("password") && body.get("password") != null && !((String) body.get("password")).isEmpty()) {
                    sql.append(", password = ?");
                    params.add(body.get("password"));
                }

                sql.append(" WHERE user_id = ?");
                params.add(userId);

                PreparedStatement pstmt = connection.prepareStatement(sql.toString());
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }

                int affectedRows = pstmt.executeUpdate();
                Map<String, Object> response = new HashMap<>();

                if (affectedRows > 0) {
                    response.put("status", "success");
                    response.put("message", "User updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "User not found");
                }

                res.type("application/json");
                return gson.toJson(response);

            } catch (SQLException e) {
                e.printStackTrace();
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Failed to update user");
                res.type("application/json");
                return gson.toJson(response);
            }
        });

        // Delete user (admin only)
        delete("/api/users/:id", (req, res) -> {
            User adminUser = req.session().attribute("user");
            if (adminUser == null || !"ADMIN".equals(adminUser.getRole())) {
                res.status(403);
                return gson.toJson(Map.of("status", "error", "message", "Access denied"));
            }

            int userId = Integer.parseInt(req.params(":id"));

            try {
                String sql = "UPDATE users SET is_active = 0 WHERE user_id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, userId);

                int affectedRows = pstmt.executeUpdate();
                Map<String, Object> response = new HashMap<>();

                if (affectedRows > 0) {
                    response.put("status", "success");
                    response.put("message", "User deactivated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "User not found");
                }

                res.type("application/json");
                return gson.toJson(response);

            } catch (SQLException e) {
                e.printStackTrace();
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Failed to deactivate user");
                res.type("application/json");
                return gson.toJson(response);
            }
        });

        // Add book (admin only)
        post("/api/books", (req, res) -> {
            User adminUser = req.session().attribute("user");
            if (adminUser == null || !"ADMIN".equals(adminUser.getRole())) {
                res.status(403);
                return gson.toJson(Map.of("status", "error", "message", "Access denied"));
            }

            Map<String, Object> body = gson.fromJson(req.body(), Map.class);

            try {
                String sql = "INSERT INTO books (title, author, genre, price, stock_quantity, description) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, (String) body.get("title"));
                pstmt.setString(2, (String) body.get("author"));
                pstmt.setString(3, (String) body.get("genre"));
                pstmt.setDouble(4, ((Number) body.get("price")).doubleValue());
                pstmt.setInt(5, ((Number) body.get("stockQuantity")).intValue());
                pstmt.setString(6, (String) body.get("description"));

                int affectedRows = pstmt.executeUpdate();
                Map<String, Object> response = new HashMap<>();

                if (affectedRows > 0) {
                    response.put("status", "success");
                    response.put("message", "Book added successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to add book");
                }

                res.type("application/json");
                return gson.toJson(response);

            } catch (SQLException e) {
                e.printStackTrace();
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Database error: " + e.getMessage());
                res.type("application/json");
                return gson.toJson(response);
            }
        });

        // Update book (admin only)
        put("/api/books/:id", (req, res) -> {
            User adminUser = req.session().attribute("user");
            if (adminUser == null || !"ADMIN".equals(adminUser.getRole())) {
                res.status(403);
                return gson.toJson(Map.of("status", "error", "message", "Access denied"));
            }

            int bookId = Integer.parseInt(req.params(":id"));
            Map<String, Object> body = gson.fromJson(req.body(), Map.class);

            try {
                String sql = "UPDATE books SET title = ?, author = ?, genre = ?, price = ?, stock_quantity = ?, description = ? WHERE book_id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, (String) body.get("title"));
                pstmt.setString(2, (String) body.get("author"));
                pstmt.setString(3, (String) body.get("genre"));
                pstmt.setDouble(4, ((Number) body.get("price")).doubleValue());
                pstmt.setInt(5, ((Number) body.get("stockQuantity")).intValue());
                pstmt.setString(6, (String) body.get("description"));
                pstmt.setInt(7, bookId);

                int affectedRows = pstmt.executeUpdate();
                Map<String, Object> response = new HashMap<>();

                if (affectedRows > 0) {
                    response.put("status", "success");
                    response.put("message", "Book updated successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Book not found");
                }

                res.type("application/json");
                return gson.toJson(response);

            } catch (SQLException e) {
                e.printStackTrace();
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Database error: " + e.getMessage());
                res.type("application/json");
                return gson.toJson(response);
            }
        });

        // Delete book (admin only)
        delete("/api/books/:id", (req, res) -> {
            User adminUser = req.session().attribute("user");
            if (adminUser == null || !"ADMIN".equals(adminUser.getRole())) {
                res.status(403);
                return gson.toJson(Map.of("status", "error", "message", "Access denied"));
            }

            int bookId = Integer.parseInt(req.params(":id"));

            try {
                String sql = "DELETE FROM books WHERE book_id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, bookId);

                int affectedRows = pstmt.executeUpdate();
                Map<String, Object> response = new HashMap<>();

                if (affectedRows > 0) {
                    response.put("status", "success");
                    response.put("message", "Book deleted successfully");
                } else {
                    response.put("status", "error");
                    response.put("message", "Book not found");
                }

                res.type("application/json");
                return gson.toJson(response);

            } catch (SQLException e) {
                e.printStackTrace();
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Database error: " + e.getMessage());
                res.type("application/json");
                return gson.toJson(response);
            }
        });

        // Logout route
        get("/api/logout", (req, res) -> {
            req.session().invalidate();
            res.redirect("/customer-login.html");
            return null;
        });

        // Default route -> Customer login homepage
        get("/", (req, res) -> {
            res.redirect("/customer-login.html");
            return null;
        });

        // Initialize controllers
        new AuthController();
        new BookController();
        new CartController();
        new OrderController();
        new InventoryController();
        new MarketingController();
        new DeliveryController();
        new ReviewController();
        new SupplierController();
        new PerformanceController();
        new DeliveryAssignmentController();
        new SystemLogController();
        new EmailCampaignController();
        new BookSupplierController();
    }

    // Helper method for customer registration
    private static void handleCustomerRegistration(spark.Request req, spark.Response res, Map<String, String> body) {
        try {
            // Check if username exists
            String checkUserSql = "SELECT COUNT(*) as count FROM users WHERE username = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkUserSql);
            checkStmt.setString(1, body.get("username"));
            ResultSet userRs = checkStmt.executeQuery();

            if (userRs.next() && userRs.getInt("count") > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Username already exists");
                res.type("application/json");
                res.body(gson.toJson(response));
                return;
            }

            // Check if email exists
            String checkEmailSql = "SELECT COUNT(*) as count FROM users WHERE email = ?";
            PreparedStatement emailStmt = connection.prepareStatement(checkEmailSql);
            emailStmt.setString(1, body.get("email"));
            ResultSet emailRs = emailStmt.executeQuery();

            if (emailRs.next() && emailRs.getInt("count") > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Email already exists");
                res.type("application/json");
                res.body(gson.toJson(response));
                return;
            }

            String sql = "INSERT INTO users (first_name, last_name, username, email, phone, role, password) VALUES (?, ?, ?, ?, ?, 'CUSTOMER', ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, body.get("firstName"));
            pstmt.setString(2, body.get("lastName"));
            pstmt.setString(3, body.get("username"));
            pstmt.setString(4, body.get("email"));
            pstmt.setString(5, body.get("phone"));
            pstmt.setString(6, body.get("password"));

            int affectedRows = pstmt.executeUpdate();
            Map<String, Object> response = new HashMap<>();

            if (affectedRows > 0) {
                // Create customer record
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        createRoleSpecificRecord(userId, "CUSTOMER", connection);
                    }
                }
                response.put("status", "success");
                response.put("message", "Registration successful! Please login.");
                System.out.println("Customer registration successful for: " + body.get("username"));
            } else {
                response.put("status", "error");
                response.put("message", "Registration failed");
                System.out.println("Customer registration failed for: " + body.get("username"));
            }

            res.type("application/json");
            res.body(gson.toJson(response));

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error in customer registration: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            if (e.getMessage().contains("unique constraint") || e.getMessage().contains("duplicate")) {
                response.put("status", "error");
                response.put("message", "Username or email already exists");
            } else {
                response.put("status", "error");
                response.put("message", "Registration error: " + e.getMessage());
            }
            res.type("application/json");
            res.body(gson.toJson(response));
        }
    }

    // Helper method for staff registration
    private static void handleStaffRegistration(spark.Request req, spark.Response res, Map<String, String> body) {
        // Validate role
        String role = body.get("role");
        List<String> validStaffRoles = Arrays.asList("ADMIN", "INVENTORY_MANAGER", "MARKETING_MANAGER", "DELIVERY_STAFF");

        if (!validStaffRoles.contains(role)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Invalid staff role selected");
            res.type("application/json");
            res.body(gson.toJson(response));
            return;
        }

        try {
            // Check if username exists
            String checkUserSql = "SELECT COUNT(*) as count FROM users WHERE username = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkUserSql);
            checkStmt.setString(1, body.get("username"));
            ResultSet userRs = checkStmt.executeQuery();

            if (userRs.next() && userRs.getInt("count") > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Username already exists");
                res.type("application/json");
                res.body(gson.toJson(response));
                return;
            }

            // Check if email exists
            String checkEmailSql = "SELECT COUNT(*) as count FROM users WHERE email = ?";
            PreparedStatement emailStmt = connection.prepareStatement(checkEmailSql);
            emailStmt.setString(1, body.get("email"));
            ResultSet emailRs = emailStmt.executeQuery();

            if (emailRs.next() && emailRs.getInt("count") > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Email already exists");
                res.type("application/json");
                res.body(gson.toJson(response));
                return;
            }

            String sql = "INSERT INTO users (first_name, last_name, username, email, phone, role, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, body.get("firstName"));
            pstmt.setString(2, body.get("lastName"));
            pstmt.setString(3, body.get("username"));
            pstmt.setString(4, body.get("email"));
            pstmt.setString(5, body.get("phone"));
            pstmt.setString(6, body.get("role"));
            pstmt.setString(7, body.get("password"));

            int affectedRows = pstmt.executeUpdate();
            Map<String, Object> response = new HashMap<>();

            if (affectedRows > 0) {
                // Create role-specific record
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        createRoleSpecificRecord(userId, role, connection);
                    }
                }
                response.put("status", "success");
                response.put("message", "Staff registration successful! Please login.");
                System.out.println("Staff registration successful for: " + body.get("username") + ", Role: " + role);
            } else {
                response.put("status", "error");
                response.put("message", "Registration failed");
                System.out.println("Staff registration failed for: " + body.get("username"));
            }

            res.type("application/json");
            res.body(gson.toJson(response));

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error in staff registration: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            if (e.getMessage().contains("unique constraint") || e.getMessage().contains("duplicate")) {
                response.put("status", "error");
                response.put("message", "Username or email already exists");
            } else {
                response.put("status", "error");
                response.put("message", "Registration error: " + e.getMessage());
            }
            res.type("application/json");
            res.body(gson.toJson(response));
        }
    }
}