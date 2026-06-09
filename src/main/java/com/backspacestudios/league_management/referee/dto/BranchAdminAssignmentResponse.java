package com.backspacestudios.league_management.referee.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BranchAdminAssignmentResponse {
    private UUID assignmentId;
    private UUID userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private UUID branchId;
    private UUID assignedBy;
    private LocalDateTime assignedAt;
}