package com.backspacestudios.league_management.core.dto;

import com.backspacestudios.league_management.core.enums.UserRole;
import com.backspacestudios.league_management.core.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserUpdateRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String email;

    private UserRole role;

    private UserStatus userStatus;

    private String phoneNumber;

    private LocalDate dateOfBirth;
}