package com.onek.ak.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
public class LogoutController {

    @GetMapping("/local-logout")  // Changed from @PostMapping
    public void localLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. Invalidate current session
        request.getSession().invalidate();

        // 2. Clear any authentication cookies
        response.setContentType("text/html");
        response.addCookie(createExpiredCookie("JSESSIONID"));
        response.addCookie(createExpiredCookie("remember-me"));

        // 3. Redirect to base URL (will show login page)
        response.sendRedirect(request.getContextPath() + "/");
    }

    private Cookie createExpiredCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setMaxAge(0); // Immediately expire
        return cookie;
    }
}