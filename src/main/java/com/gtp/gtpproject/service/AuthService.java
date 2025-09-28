package com.gtp.gtpproject.service;

import com.example.booknest.dao.UserDAO;
import com.example.booknest.model.User;
import com.example.booknest.util.PasswordHasher;
import com.example.booknest.util.ValidationUtil;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public Map<String, Object> registerUser(User user) {
        Map<String, Object> response = new HashMap<>();

        // Validate input
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            response.put("status", "error");
            response.put("message", "Invalid email format");
            return response;
        }

        if (!ValidationUtil.isValidPassword(user.getPassword())) {
            response.put("status", "error");
            response.put("message", "Password must be at least 6 characters long");
            return response;
        }

        if (user.getPhone() != null && !user.getPhone().isEmpty() &&
                !ValidationUtil.isValidPhone(user.getPhone())) {
            response.put("status", "error");
            response.put("message", "Invalid phone number format");
            return response;
        }

        // Check if username exists
        if (userDAO.isUsernameExists(user.getUsername())) {
            response.put("status", "error");
            response.put("message", "Username already exists");
            return response;
        }

        // Check if email exists
        if (userDAO.isEmailExists(user.getEmail())) {
            response.put("status", "error");
            response.put("message", "Email already exists");
            return response;
        }

        // Hash password before storing
        String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        // Register user
        boolean success = userDAO.registerUser(user);
        if (success) {
            response.put("status", "success");
            response.put("message", "Registration successful! Please login.");
        } else {
            response.put("status", "error");
            response.put("message", "Registration failed. Please try again.");
        }

        return response;
    }

    public Map<String, Object> loginUser(String username, String password) {
        Map<String, Object> response = new HashMap<>();

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Username and password are required");
            return response;
        }

        // Hash the provided password for comparison
        String hashedPassword = PasswordHasher.hashPassword(password);
        User user = userDAO.loginUser(username, hashedPassword);

        if (user != null) {
            response.put("status", "success");
            response.put("message", "Login successful!");
            response.put("user", user);
        } else {
            response.put("status", "error");
            response.put("message", "Invalid username or password");
        }

        return response;
    }

    public Map<String, Object> validateSession(User user) {
        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            response.put("authenticated", true);
            response.put("user", user);
        } else {
            response.put("authenticated", false);
        }

        return response;
    }
}