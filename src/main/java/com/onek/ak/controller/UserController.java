package com.onek.ak.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.List;

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
        String uid = (String) session.getAttribute("uid");
        String title = (String) session.getAttribute("title");
        String photo = (String) session.getAttribute("photo");
        Object groupsObj = session.getAttribute("groups");

        // Handle null values (session expired or user not logged in)
        if (role == null) role = "Sales";
        if (firstname == null) firstname = "Unknown";
        if (lastname == null) lastname = "Unknown";
        if (email == null) email = "Unknown";
        if (uid == null) uid = "001";
        if (title == null) title = "Employee";
        if (photo == null) photo = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAACXBIWXMAAAsTAAALEwEAmpwYAAAByElEQVR4nJWSXU8TQRSGv28XLQpRSgKhQKBgEErYoBS12YJ27t2I/4H/CX2Aj9ArWoECFKSvQGp1gEOLlF2BQFAURJghBgFYUQTRd3Z2ZHmh8mVmZ3X3c+7M7sTMGkAXFMy6bgLbABPjTFeu4ADGAn6KuAgtl/qV5Wc+gR1Drq9JKfcdl0dhdIUV1ngFoAZ1XcE1zR8dQoYex7AZH6NH8He0Gm8G7WuoOOhMNvSmiTAxYGdyq1QmgEkBvE2Hq6rs+2leJ1Qb6ZlOs0uN4JmMQ5lxdHgQe0LT+p5FDEAF0xKMVpMeEebSxaVZ9H1D96/AeBV9vwhMQAiKlvIPi2n+mfgyqvd1LRk7zDImb30Jbm38AZ57IdUuL0CuXYGG1LwGAWtTtbgO4JPFrGQHR59lK6TisjwMLHfbJzHGLK5KlfdxFw4sBh54EoHHxAXXROxaNbtpZykcn3cldpPlUHg+bocq2KeDBzNk53MueZPyHRovskBeNoa+tI5xxlsBgq3NyHFZ6EkBVgNYa6yNxR8QXU/EkBrwCrqguA6+gDZwBvKm0NcE1cgPyCrpkeV7AuGm5b6yNC4Psyy66puJxMTMbPpxakIE6vVddqGzVo2tVWcdZ50hEXHp2VH3v7gpe7dfVN+VzvLwr1ae4DW9C0K25ctAAAAAElFTkSuQmCC";

        // Handle groups (could be List<String> or null)
        List<String> groups = null;
        if (groupsObj instanceof List) {
            groups = (List<String>) groupsObj;
        } else if (groupsObj instanceof String) {
            groups = List.of((String) groupsObj);
        }
        if (groups == null) {
            groups = List.of("Common", "Admin"); // Empty list if no groups
        }

        // Normalize role
        if (role.toLowerCase().contains("sales")) {
            role = "Sales";
        } else if (role.toLowerCase().contains("marketing")) {
            role = "Marketing";
        } else {
            role = "Sales"; // Default role
        }

        // Return the user profile as JSON
        return Map.of(
                "firstname", firstname,
                "lastname", lastname,
                "email", email,
                "role", role,
                "uid", uid,
                "title", title,
                "photo", photo,
                "groups", groups
        );
    }
}