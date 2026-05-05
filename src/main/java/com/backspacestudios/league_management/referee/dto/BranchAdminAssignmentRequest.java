package com.backspacestudios.league_management.referee.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BranchAdminAssignmentRequest {
    private UUID userId;
    private UUID branchId;
}