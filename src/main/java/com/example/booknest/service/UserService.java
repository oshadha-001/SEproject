package com.example.booknest.service;

import com.example.booknest.dao.UserDAO;
import com.example.booknest.model.User;
import com.example.booknest.util.PasswordHasher;
import com.example.booknest.util.ValidationUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public Map<String, Object> registerUser(User user) {
        Map<String, Object> response = new HashMap<>();

        // Validate input
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Username is required");
            return response;
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email is required");
            return response;
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Password is required");
            return response;
        }

        // Validate email format
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            response.put("status", "error");
            response.put("message", "Invalid email format");
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

        // Hash password
        String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        // Register user
        boolean success = userDAO.registerUser(user);
        if (success) {
            response.put("status", "success");
            response.put("message", "User registered successfully");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to register user");
        }

        return response;
    }

    public User loginUser(String username, String password) {
        String hashedPassword = PasswordHasher.hashPassword(password);
        return userDAO.loginUser(username, hashedPassword);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }

    public boolean deactivateUser(int userId) {
        return userDAO.deactivateUser(userId);
    }

    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }
}