package com.backspacestudios.league_management.core.dto;

import java.time.LocalDate;

import com.backspacestudios.league_management.core.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private UserRole role;

    private String phoneNumber;
        private LocalDate dateOfBirth;
    
}
