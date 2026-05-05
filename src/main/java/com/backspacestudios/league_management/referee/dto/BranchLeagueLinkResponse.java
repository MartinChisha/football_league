package com.backspacestudios.league_management.referee.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BranchLeagueLinkResponse {
    private UUID linkId;
    private UUID branchId;
    private String branchName;
    private UUID leagueId;
    private String leagueName;
    private UUID divisionId;
    private String divisionName;
    private Boolean active;
    private LocalDateTime createdAt;
}