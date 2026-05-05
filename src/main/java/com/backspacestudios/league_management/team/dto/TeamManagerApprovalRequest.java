package com.backspacestudios.league_management.team.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TeamManagerApprovalRequest {
    @NotNull
    private UUID userId;      // the user being assigned
    @NotNull
    private UUID teamId;
    private boolean approve = true;  // true = approve, false = reject
}