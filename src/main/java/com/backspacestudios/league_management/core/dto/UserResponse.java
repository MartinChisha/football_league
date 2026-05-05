package com.backspacestudios.league_management.core.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.backspacestudios.league_management.core.enums.UserRole;
import com.backspacestudios.league_management.core.enums.UserStatus;

import lombok.Data;

@Data
public class UserResponse {
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private UserRole role;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private UserStatus userStatus;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
