package com.gtp.gtpproject.controller;

import com.example.booknest.model.User;
import com.example.booknest.service.AuthService;
import com.google.gson.Gson;

import java.util.Map;

import static spark.Spark.*;

public class AuthController {
    private AuthService authService;
    private Gson gson;

    public AuthController() {
        this.authService = new AuthService();
        this.gson = new Gson();
        setupRoutes();
    }

    private void setupRoutes() {
        // ... existing setup code ...

        post("/api/register", (req, res) -> {
            res.type("application/json");
            try {
                User user = gson.fromJson(req.body(), User.class);
                Map<String, Object> response = authService.registerUser(user);
                return gson.toJson(response);
            } catch (Exception e) {
                Map<String, String> response = Map.of(
                        "status", "error",
                        "message", "Invalid request data: " + e.getMessage()
                );
                return gson.toJson(response);
            }
        });

        post("/api/login", (req, res) -> {
            res.type("application/json");
            try {
                Map<String, String> loginData = gson.fromJson(req.body(), Map.class);
                String username = loginData.get("username");
                String password = loginData.get("password");

                Map<String, Object> response = authService.loginUser(username, password);

                if ("success".equals(response.get("status"))) {
                    req.session().attribute("user", response.get("user"));
                }

                return gson.toJson(response);
            } catch (Exception e) {
                Map<String, String> response = Map.of(
                        "status", "error",
                        "message", "Login failed. Please try again."
                );
                return gson.toJson(response);
            }
        });

        // ... other routes ...
    }
}