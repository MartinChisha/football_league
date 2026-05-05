package com.backspacestudios.league_management.league.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DivisionApprovalRequest {
    @NotNull
    private UUID divisionId;
    private boolean approve = true; // true = active, false = reject (set status to inactive)
}