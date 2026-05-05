package com.backspacestudios.league_management.team.dto;

import com.backspacestudios.league_management.team.enums.ManagerStatus;
import com.backspacestudios.league_management.team.enums.ManagerType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TeamManagerResponse {
    private UUID userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private UUID teamId;
    private String teamName;
    private ManagerType managerType;
    private Boolean canManageRoster;
    private Boolean canViewFinancials;
    private Boolean canCommunicateLeague;
    private LocalDate contractExpiryDate;
    private UUID assignedBy;
    private LocalDateTime assignedAt;
    private ManagerStatus status;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
}