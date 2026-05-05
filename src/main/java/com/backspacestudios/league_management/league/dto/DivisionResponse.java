package com.backspacestudios.league_management.league.dto;

import com.backspacestudios.league_management.league.enums.DivisionStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class DivisionResponse {
    private UUID divisionId;
    private UUID leagueId;
    private UUID parentDivisionId;
    private String divisionName;
    private String divisionCode;
    private Integer divisionLevel;
    private String description;
    private Integer promotionSpots;
    private Integer relegationSpots;
    private Integer maxTeams;
    private Integer minTeams;
    private Map<String, Object> sortingRules;
    private DivisionStatus status;
    private Map<String, Object> divisionConfig;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}