package com.onek.ak.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    public Map<String, Object> getUserProfile(HttpSession session) {
        // Retrieve attributes from session
        String firstname = (String) session.getAttribute("firstname");
        String lastname = (String) session.getAttribute("lastname");
        String email = (String) session.getAttribute("email");
        String role = (String) session.getAttribute("role");

        // Handle null values (session expired or user not logged in)
        if (role == null) role = "guest";
        if (firstname == null) firstname = "Unknown";
        if (lastname == null) lastname = "Unknown";
        if (email == null) email = "Unknown";

        // Return the user profile as JSON
        return Map.of(
                "firstname", firstname,
                "lastname", lastname,
                "email", email,
                "role", role
        );
    }
}
