package com.backspacestudios.league_management.competition.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class FixtureSummary {
    private Integer matchWeek;
    private UUID homeTeamId;
    private UUID awayTeamId;
}
