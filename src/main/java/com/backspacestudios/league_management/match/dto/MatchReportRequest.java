package com.backspacestudios.league_management.match.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class MatchReportRequest {
    private UUID fixtureId;
    private int homeScore;
    private int awayScore;
    private String reportStatus;
    private LocalDateTime matchStartTime;
    private LocalDateTime matchEndTime;
    private Integer attendance;
    private String weatherConditions;
    private Integer homePossession, awayPossession;
    private Integer homeShots, awayShots;
    private Integer homeShotsOnTarget, awayShotsOnTarget;
    private Integer homeFouls, awayFouls;
    private Integer homeCorners, awayCorners;
    private Integer homeOffsides, awayOffsides;
    private List<MatchEventDto> events;
}