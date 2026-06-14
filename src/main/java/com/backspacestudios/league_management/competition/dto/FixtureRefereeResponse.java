package com.backspacestudios.league_management.competition.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data @Builder
public class FixtureRefereeResponse {
    private UUID assignmentId;
    private UUID fixtureId;
    private UUID refereeId;
    private String refereeName;
    private String role;
    private String assignedAt;
}