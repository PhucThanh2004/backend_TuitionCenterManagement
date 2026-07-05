package com.management.student_center.security;

import com.management.student_center.entity.User;
import com.management.student_center.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("=== JwtFilter === Request path: " + path);
        System.out.println("=== JwtFilter === Method: " + request.getMethod());

        // Bỏ qua login
        if (path.equals("/v1/api/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Lấy token từ nhiều nguồn
        String token = null;

        // 1️⃣ Từ Header Authorization (Ưu tiên)
        String authHeader = request.getHeader("Authorization");
        System.out.println("=== JwtFilter === Authorization Header: " + authHeader);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("=== JwtFilter === Token from Header: " + token.substring(0, Math.min(token.length(), 20)) + "...");
        }

        // 2️⃣ Từ Cookie (Fallback)
        if (token == null && request.getCookies() != null) {
            Optional<Cookie> tokenCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("token"))
                    .findFirst();
            
            if (tokenCookie.isPresent()) {
                token = tokenCookie.get().getValue();
                System.out.println("=== JwtFilter === Token from Cookie: " + token.substring(0, Math.min(token.length(), 20)) + "...");
            }
        }

        if (token == null) {
            System.out.println("=== JwtFilter === No token found in request");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            System.out.println("=== JwtFilter === Processing token...");
            
            // ✅ Lấy userId từ token
            Long userId = JwtUtil.getUserId(token, jwtSecret);
            System.out.println("=== JwtFilter === User ID from token: " + userId);
            
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("=== JwtFilter === User found: " + user.getEmail());

                // 1️⃣ Set attribute để controller dùng
                request.setAttribute("user", user);

                // 2️⃣ Authenticate Spring Security
                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoleId()));
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                System.out.println("=== JwtFilter === Authentication set successfully");

            } else {
                System.out.println("=== JwtFilter === User not found with ID: " + userId);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Người dùng không tồn tại");
                return;
            }
        } catch (Exception e) {
            System.out.println("=== JwtFilter === Token validation error: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}