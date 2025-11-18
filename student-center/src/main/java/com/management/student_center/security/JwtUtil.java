package com.management.student_center.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // Tạo key từ secret string
    public static Key getKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static String generateToken(Long userId, String email, String roleId, String secret, long expirationMillis) {
        Key key = getKey(secret);
        return Jwts.builder()
                .claim("id", userId)
                .claim("email", email)
                .claim("roleId", roleId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Long getUserId(String token, String secret) {
        Key key = getKey(secret);
        return ((Number) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id")).longValue();
    }
}
