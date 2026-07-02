package com.backspacestudios.league_management.match.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data @Builder
public class LineupResponse {
    private UUID lineupId;
    private UUID fixtureId;
    private UUID teamId;
    private UUID submittedBy;
    private LocalDateTime submittedAt;
    private String status;
    private List<LineupRequest.LineupPlayer> startingEleven;
    private List<LineupRequest.LineupPlayer> substitutes;
    private List<LineupRequest.TechnicalStaff> technicalStaff;
    private int version;
}