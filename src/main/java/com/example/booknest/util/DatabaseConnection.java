package com.example.booknest.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=bookNest;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "789";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("SQL Server JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQL Server JDBC driver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connected successfully to: bookNest");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Connection test successful!");
            } else {
                System.out.println("❌ Connection test failed!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Connection test failed: " + e.getMessage());
        }
    }

    public static void initializeDatabase() {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                // Read and execute the schema file
                InputStream inputStream = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("database_schema.sql");
                
                if (inputStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder schema = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        schema.append(line).append("\n");
                    }
                    reader.close();
                    
                    // Split by semicolon and execute each statement
                    String[] statements = schema.toString().split(";");
                    Statement stmt = conn.createStatement();
                    
                    for (String statement : statements) {
                        statement = statement.trim();
                        if (!statement.isEmpty() && !statement.startsWith("--")) {
                            try {
                                stmt.execute(statement);
                            } catch (SQLException e) {
                                // Ignore errors for existing tables/indexes
                                if (!e.getMessage().contains("already exists") && 
                                    !e.getMessage().contains("duplicate")) {
                                    System.out.println("Warning: " + e.getMessage());
                                }
                            }
                        }
                    }
                    stmt.close();
                    System.out.println("Database schema initialized successfully");
                } else {
                    System.out.println("Warning: database_schema.sql not found, using existing database");
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}