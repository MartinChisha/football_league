package com.backspacestudios.league_management.league.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

import com.backspacestudios.league_management.league.enums.FinancialAccessLevel;

@Data
public class LeagueAdminRequest {
    @NotNull
    private UUID userId;

    @NotNull
    private UUID leagueId;

    private Map<String, Object> adminPermissions;

    private Boolean canManageReferees = true;

    private Boolean canManageTeams = true;

    private Boolean canScheduleFixtures = true;

    private FinancialAccessLevel financialAccessLevel = FinancialAccessLevel.none;
}