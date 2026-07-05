package com.management.student_center.security;

import com.management.student_center.entity.User;
import com.management.student_center.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
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

        // Bỏ qua login
        if (path.equals("/v1/api/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ LẤY TOKEN TỪ HEADER AUTHORIZATION
        String token = null;
        String authHeader = request.getHeader("Authorization");
        System.out.println("=== JwtFilter === Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("=== JwtFilter === Token from Header: " + token.substring(0, Math.min(token.length(), 20)) + "...");
        }

        // Fallback: lấy từ Cookie
        if (token == null && request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                    System.out.println("=== JwtFilter === Token from Cookie: " + token.substring(0, Math.min(token.length(), 20)) + "...");
                }
            }
        }

        if (token != null) {
            try {
                Long userId = JwtUtil.getUserId(token, jwtSecret);
                System.out.println("=== JwtFilter === User ID from token: " + userId);
                
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    System.out.println("=== JwtFilter === User found: " + user.getEmail());

                    request.setAttribute("user", user);

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
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                return;
            }
        } else {
            System.out.println("=== JwtFilter === No token found in request");
        }

        filterChain.doFilter(request, response);
    }
}