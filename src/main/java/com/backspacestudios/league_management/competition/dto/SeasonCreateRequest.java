package com.backspacestudios.league_management.competition.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

import com.backspacestudios.league_management.competition.enums.FixtureGenerationType;

@Data
public class SeasonCreateRequest {

    @NotNull
    private UUID divisionId;

    @NotBlank
    private String name;

    @NotNull
    @Min(1900)
    private Integer year;

    private LocalDate startDate;

    private LocalDate endDate;

    // optional fixture generation config for later
    private FixtureGenerationType generationType = FixtureGenerationType.ROUND_ROBIN_SINGLE;
}