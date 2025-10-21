package com.example.booknest.service;

import com.example.booknest.dao.UserDAO;
import com.example.booknest.model.User;
import com.example.booknest.util.PasswordHasher;
import com.example.booknest.util.ValidationUtil;

import java.util.HashMap;
import java.util.List;
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

    public Map<String, Object> updateUser(User user, boolean updatePassword) {
        Map<String, Object> response = new HashMap<>();

        // Validate input
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            response.put("status", "error");
            response.put("message", "Invalid email format");
            return response;
        }

        if (user.getPhone() != null && !user.getPhone().isEmpty() &&
                !ValidationUtil.isValidPhone(user.getPhone())) {
            response.put("status", "error");
            response.put("message", "Invalid phone number format");
            return response;
        }

        // Check if email exists for other users
        User existingUser = userDAO.getUserById(user.getUserId());
        if (existingUser != null && !existingUser.getEmail().equals(user.getEmail())) {
            if (userDAO.isEmailExists(user.getEmail())) {
                response.put("status", "error");
                response.put("message", "Email already exists");
                return response;
            }
        }

        boolean success;
        if (updatePassword && user.getPassword() != null && !user.getPassword().isEmpty()) {
            if (!ValidationUtil.isValidPassword(user.getPassword())) {
                response.put("status", "error");
                response.put("message", "Password must be at least 6 characters long");
                return response;
            }
            String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
            success = userDAO.updateUserWithPassword(user);
        } else {
            success = userDAO.updateUser(user);
        }

        if (success) {
            response.put("status", "success");
            response.put("message", "User updated successfully");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to update user");
        }

        return response;
    }

    public Map<String, Object> deleteUser(int userId) {
        Map<String, Object> response = new HashMap<>();

        boolean success = userDAO.deleteUser(userId);
        if (success) {
            response.put("status", "success");
            response.put("message", "User deleted successfully");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to delete user");
        }

        return response;
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
}