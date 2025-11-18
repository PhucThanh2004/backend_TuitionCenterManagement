package com.management.student_center.dto;

public record UpdateProfileRequest(
        String fullName,
        String phoneNumber,
        Boolean gender,
        String roleId,
        String email
) {}

