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

        // Bỏ qua login/logout
        if (path.equals("/v1/api/login") || path.equals("/v1/api/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy token từ cookie
        String token = null;
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                }
            }
        }
     // ===== DEBUG =====
        System.out.println("Token from cookie: " + token);
        System.out.println("SecurityContext BEFORE auth: " + SecurityContextHolder.getContext().getAuthentication());

        if (token != null) {
            try {
                Long userId = JwtUtil.getUserId(token, jwtSecret);
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    // 1️⃣ Set attribute để controller dùng
                    request.setAttribute("user", user);

                    // 2️⃣ Authenticate Spring Security
                    List<SimpleGrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority(user.getRoleId()));
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Người dùng không tồn tại");
                    return;
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
