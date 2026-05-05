package com.backspacestudios.league_management.team.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.backspacestudios.league_management.team.enums.ManagerType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamManagerRequest {
    @NotNull
    private UUID teamId;
 
    @Email
    @NotNull
    private String userEmail;

    private ManagerType managerType;
    private Boolean canManageRoster;
    private Boolean canViewFinancials;
    private Boolean canCommunicateLeague;
    private LocalDate contractExpiryDate;
}
