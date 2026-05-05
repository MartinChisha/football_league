package com.backspacestudios.league_management.referee.dto;

import com.backspacestudios.league_management.referee.enums.RefereeClass;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RefereeResponse {
    private UUID refereeId;
    private UUID userId;
    private String refereeCode;
    private RefereeClass currentClass;
    private LocalDate dateOfBirth;
    private String nationality;
    private LocalDateTime createdAt;
}