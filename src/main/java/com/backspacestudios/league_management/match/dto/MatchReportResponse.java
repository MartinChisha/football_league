package com.backspacestudios.league_management.match.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data @Builder
public class MatchReportResponse {
    private UUID reportId;
    private UUID fixtureId;
    private int homeScore, awayScore;
    private String reportStatus;
    private LocalDateTime matchStartTime, matchEndTime;
    private Integer attendance;
    private String weatherConditions;
    private Integer homePossession, awayPossession;
    private Integer homeShots, awayShots;
    private Integer homeShotsOnTarget, awayShotsOnTarget;
    private Integer homeFouls, awayFouls;
    private Integer homeCorners, awayCorners;
    private Integer homeOffsides, awayOffsides;
    private List<MatchEventDto> events;
    private LocalDateTime createdAt, updatedAt;
}