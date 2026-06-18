package com.backspacestudios.league_management.competition.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixtureSummary {
    private UUID fixtureId;
    private UUID seasonId;
    private int matchWeek;
    private UUID homeTeamId;
    private UUID awayTeamId;
    private String status;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private Integer homeScore;
    private Integer awayScore;
    private String venue;
}