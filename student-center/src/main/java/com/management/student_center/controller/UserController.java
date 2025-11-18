package com.management.student_center.controller;

import com.management.student_center.dto.UpdateProfileRequest;
import com.management.student_center.dto.VerifyEmailOtpRequest;
import com.management.student_center.service.UserService;
import com.management.student_center.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/api/profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> getUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserById(user.getId()));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileRequest dto
    ) {
        return ResponseEntity.ok(userService.updateUserProfile(user.getId(), dto));
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<?> verifyEmailOtp(
            @AuthenticationPrincipal User user,
            @RequestBody VerifyEmailOtpRequest dto
    ) {
        return ResponseEntity.ok(userService.verifyEmailOtp(user.getId(), dto.otp()));
    }

    @PutMapping("/image")
    public ResponseEntity<?> updateImageProfile(
            @AuthenticationPrincipal User user,
            @RequestParam("image") MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(userService.updateUserImage(user.getId(), file));
    }
}
