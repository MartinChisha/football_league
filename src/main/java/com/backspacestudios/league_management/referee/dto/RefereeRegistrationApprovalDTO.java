package com.backspacestudios.league_management.referee.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RefereeRegistrationApprovalDTO {
    private UUID requestId;
    private boolean approved;
    private String rejectionReason; // used if approved = false
}