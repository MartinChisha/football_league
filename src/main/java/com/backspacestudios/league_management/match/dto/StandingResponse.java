package com.backspacestudios.league_management.match.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data @Builder
public class StandingResponse {
    private UUID standingId;
    private UUID seasonId;
    private UUID teamId;
    private String teamName;
    private int played, wins, draws, losses;
    private int goalsFor, goalsAgainst, goalDifference, points;
    private int winsHome, drawsHome, lossesHome, goalsForHome, goalsAgainstHome;
    private int winsAway, drawsAway, lossesAway, goalsForAway, goalsAgainstAway;
}