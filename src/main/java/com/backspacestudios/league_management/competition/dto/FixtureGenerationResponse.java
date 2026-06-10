package com.backspacestudios.league_management.competition.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
@Data @AllArgsConstructor
public class FixtureGenerationResponse {
    private int totalMatches;
    private Long randomSeedUsed;
    private List<FixtureSummary> fixtures;
}