package com.gtp.gtpproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ThemeController {

    @PostMapping("/theme")
    public String setTheme(@RequestParam String theme,
                           @RequestParam(required = false) String redirect,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        request.getSession().setAttribute("theme", theme);

        // Set theme in cookie for persistence
        jakarta.servlet.http.Cookie themeCookie = new jakarta.servlet.http.Cookie("theme", theme);
        themeCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        themeCookie.setPath("/");
        response.addCookie(themeCookie);

        return redirect != null ? "redirect:" + redirect : "redirect:/inventory/dashboard";
    }
}