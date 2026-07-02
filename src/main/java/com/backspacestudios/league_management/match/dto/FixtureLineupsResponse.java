package com.backspacestudios.league_management.match.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class FixtureLineupsResponse {
    private TeamLineupDetail home;
    private TeamLineupDetail away;

    @Data
    @Builder
    public static class TeamLineupDetail {
        private UUID teamId;
        private String teamName;
        private UUID lineupId;
        private UUID submittedBy;
        private String submittedByName;
        private LocalDateTime submittedAt;
        private String status;
        private List<EnrichedLineupPlayer> startingEleven;
        private List<EnrichedLineupPlayer> substitutes;
        private List<LineupRequest.TechnicalStaff> technicalStaff;
    }

    @Data
    @Builder
    public static class EnrichedLineupPlayer {
        private UUID playerId;
        private String name;
        private String position;
        private int shirtNumber;
    }
}
