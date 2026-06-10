package com.backspacestudios.league_management.competition.dto;

import com.backspacestudios.league_management.competition.enums.SeasonStatus;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class SeasonResponse {
    private UUID seasonId;
    private UUID divisionId;
    private String name;
    private Integer seasonYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private SeasonStatus status;
    private String fixtureGenerationConfig;
    private Instant confirmedAt;
    private UUID confirmedBy;
    private Instant createdAt;
    private Instant updatedAt;
}