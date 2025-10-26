package com.gtp.gtpproject.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class ThemeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String theme = request.getParameter("theme");

        // Check URL parameter first
        if (theme != null && (theme.equals("light") || theme.equals("dark"))) {
            request.getSession().setAttribute("theme", theme);

            // Set cookie
            Cookie themeCookie = new Cookie("theme", theme);
            themeCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
            themeCookie.setPath("/");
            response.addCookie(themeCookie);
        }
        // Check session
        else {
            String sessionTheme = (String) request.getSession().getAttribute("theme");
            if (sessionTheme == null) {
                // Check cookie
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("theme".equals(cookie.getName()) &&
                                (cookie.getValue().equals("light") || cookie.getValue().equals("dark"))) {
                            request.getSession().setAttribute("theme", cookie.getValue());
                            break;
                        }
                    }
                }

                // Default theme
                if (request.getSession().getAttribute("theme") == null) {
                    request.getSession().setAttribute("theme", "light");
                }
            }
        }
        return true;
    }
}