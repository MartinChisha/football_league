package com.backspacestudios.league_management.league.dto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.backspacestudios.league_management.league.enums.FinancialAccessLevel;

@Data
public class LeagueAdminResponse {
    private UUID userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private UUID leagueId;
    private String leagueName;
    private Map<String, Object> adminPermissions;
    private Boolean canManageReferees;
    private Boolean canManageTeams;
    private Boolean canScheduleFixtures;
    private FinancialAccessLevel financialAccessLevel;
    private UUID assignedBy;
    private LocalDateTime assignedAt;
}