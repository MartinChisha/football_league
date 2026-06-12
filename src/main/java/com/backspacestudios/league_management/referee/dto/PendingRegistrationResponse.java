package com.backspacestudios.league_management.referee.dto;

import com.backspacestudios.league_management.referee.enums.RefereeClass;
import com.backspacestudios.league_management.referee.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PendingRegistrationResponse {
    private UUID requestId;
    private UUID userId;
    private UUID branchId;
    private String branchName;
    private RefereeClass requestedClass;
    private RequestStatus status;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private LocalDateTime createdAt;
}