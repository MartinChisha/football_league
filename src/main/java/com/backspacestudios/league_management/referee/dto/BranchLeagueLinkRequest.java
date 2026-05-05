package com.backspacestudios.league_management.referee.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BranchLeagueLinkRequest {
    private UUID branchId;
    private UUID leagueId;
    private UUID divisionId;  // optional
}