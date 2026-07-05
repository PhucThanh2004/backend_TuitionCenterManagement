package com.management.student_center.service;

import com.management.student_center.entity.User;
import com.management.student_center.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> handleUserLogin(String email, String password) {
        System.out.println("🔐 LoginService: Checking user with email: " + email);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("✅ User found: " + user.getEmail());
            System.out.println("🔑 Password hash from DB: " + user.getPassword());
            System.out.println("🔑 Input password length: " + (password != null ? password.length() : 0));
            
            // ✅ Kiểm tra password với BCrypt
            boolean matches = BCrypt.checkpw(password, user.getPassword());
            System.out.println("✅ Password matches: " + matches);
            
            if (matches) {
                return userOpt;
            }
        } else {
            System.out.println("❌ User not found with email: " + email);
        }
        
        return Optional.empty();
    }
}