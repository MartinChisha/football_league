package com.backspacestudios.league_management.league.dto;


import com.backspacestudios.league_management.league.enums.LeagueStatus;
import com.backspacestudios.league_management.league.enums.LeagueStructure;
import com.backspacestudios.league_management.league.enums.LeagueType;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class LeagueRequest {
    @NotBlank
    private String leagueName;

    @NotBlank
    private String leagueCode;

    private String description;

    @NotBlank
    private String countryCode;

    private String region;

    private LeagueType leagueType;

    private LeagueStructure overallStructure;

    private LeagueStatus status;

    private Integer foundedYear;

    private String logoUrl;

    private String website;

    private String contactEmail;

    private Map<String, Object> globalConfig;
}
