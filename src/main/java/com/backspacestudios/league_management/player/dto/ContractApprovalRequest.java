package com.backspacestudios.league_management.player.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ContractApprovalRequest {
    @NotNull
    private UUID contractId;
    private boolean approve = true;
}