package com.backspacestudios.league_management.team.dto;

import com.backspacestudios.league_management.team.enums.FinancialStatus;
import com.backspacestudios.league_management.team.enums.TeamStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class TeamRequest {
    @NotNull
    private UUID leagueId;

    private UUID divisionId;

    @NotBlank
    private String teamName;

    @NotBlank
    private String teamCode;

    private String shortName;
    private Integer foundedYear;
    private String homeCity;
    private String homeStadium;
    private Integer stadiumCapacity;
    private Map<String, Object> clubColors;
    private String logoUrl;
    private String website;
    private String contactEmail;
    private String phoneNumber;
    private TeamStatus status;
    private FinancialStatus financialStatus;
    private Map<String, Object> metadata;
}