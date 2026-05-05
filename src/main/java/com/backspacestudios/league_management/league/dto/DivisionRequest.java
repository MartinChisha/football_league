package com.backspacestudios.league_management.league.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class DivisionRequest {
    @NotNull
    private UUID leagueId;

    private UUID parentDivisionId;

    @NotBlank
    private String divisionName;

    @NotBlank
    private String divisionCode;

    private Integer divisionLevel = 1;
    private String description;
    private Integer promotionSpots = 0;
    private Integer relegationSpots = 0;
    private Integer maxTeams = 20;
    private Integer minTeams = 10;
    private Map<String, Object> sortingRules;
    private Map<String, Object> divisionConfig;
}