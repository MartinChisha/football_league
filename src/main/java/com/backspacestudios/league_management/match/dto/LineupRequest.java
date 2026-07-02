package com.backspacestudios.league_management.match.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class LineupRequest {
    private UUID fixtureId;
    private UUID teamId;
    private List<LineupPlayer> startingEleven;
    private List<LineupPlayer> substitutes;
    private List<TechnicalStaff> technicalStaff;
    private String status;

    @Data
    public static class LineupPlayer {
        private UUID playerId;
        private String position;
        private int shirtNumber;
    }

    @Data
    public static class TechnicalStaff {
        private String name;
        private String role;
    }
}