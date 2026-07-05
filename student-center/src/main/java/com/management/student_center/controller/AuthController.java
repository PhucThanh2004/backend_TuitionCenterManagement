package com.management.student_center.controller;

import com.management.student_center.entity.User;
import com.management.student_center.service.LoginService;
import com.management.student_center.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api")
public class AuthController {

    private final LoginService loginService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request, 
                                                      HttpServletResponse response) {
        String email = request.get("email");
        String password = request.get("password");

        System.out.println("🔐 Login attempt for: " + email);

        Optional<User> userOpt = loginService.handleUserLogin(email, password);
        Map<String, Object> res = new HashMap<>();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("✅ User found: " + user.getEmail());

            // Tạo token
            String token = JwtUtil.generateToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRoleId(),
                    jwtSecret,
                    3600 * 1000 // 1 giờ
            );

            System.out.println("🔑 Token generated: " + token.substring(0, Math.min(token.length(), 20)) + "...");

            // ✅ Gửi token qua cookie (HttpOnly)
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(3600);
            cookie.setPath("/");
            cookie.setSecure(true); // Chỉ gửi qua HTTPS
            cookie.setAttribute("SameSite", "None");
            response.addCookie(cookie);

            // ✅ Trả về token trong response body cho Frontend
            res.put("message", "Đăng nhập thành công!");
            res.put("token", token); // 🔥 QUAN TRỌNG: Trả về token
            res.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "fullName", user.getFullName(),
                    "phoneNumber", user.getPhoneNumber(),
                    "gender", user.getGender(),
                    "image", user.getImage(),
                    "roleId", user.getRoleId()
            ));

            return ResponseEntity.ok(res);
        } else {
            System.out.println("❌ Login failed for: " + email);
            res.put("message", "Email hoặc mật khẩu không đúng!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Đăng xuất thành công!");
        return res;
    }

    @GetMapping("/auth/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        System.out.println("🔐 GET /auth/me called");
        
        // Lấy user từ request attribute (được set bởi JwtFilter)
        User user = (User) request.getAttribute("user");

        if (user == null) {
            System.out.println("❌ User not found in request attribute");
            Map<String, Object> response = new HashMap<>();
            response.put("errCode", 401);
            response.put("message", "Token không hợp lệ hoặc chưa đăng nhập");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        System.out.println("✅ User found: " + user.getEmail());

        Map<String, Object> res = new HashMap<>();
        res.put("errCode", 0);
        res.put("message", "OK");
        res.put("user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "phoneNumber", user.getPhoneNumber(),
                "gender", user.getGender(),
                "image", user.getImage(),
                "roleId", user.getRoleId(),
                "createdAt", user.getCreatedAt(),
                "passwordUpdatedAt", user.getPasswordUpdatedAt()
        ));

        return ResponseEntity.ok(res);
    }
}