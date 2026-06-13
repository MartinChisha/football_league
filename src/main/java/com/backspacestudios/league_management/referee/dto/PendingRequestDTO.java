package com.backspacestudios.league_management.referee.dto;

import com.backspacestudios.league_management.referee.enums.RefereeClass;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PendingRequestDTO {
    private UUID requestId;
    private UUID userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private UUID branchId;
    private RefereeClass requestedClass;
    private LocalDateTime createdAt;
}