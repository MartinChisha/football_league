package com.backspacestudios.league_management.league.dto;


import com.backspacestudios.league_management.league.enums.LeagueStatus;
import com.backspacestudios.league_management.league.enums.LeagueStructure;
import com.backspacestudios.league_management.league.enums.LeagueType;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class LeagueResponse {
    private UUID leagueId;
    private String leagueName;
    private String leagueCode;
    private String description;
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
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}