package com.management.student_center.dto;

public record ResetPasswordRequest(String email, String otp, String newPassword) {}
