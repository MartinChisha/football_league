package com.backspacestudios.league_management.match.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class MatchEventDto {
    private String eventType;
    private int minute;
    private UUID playerId;
    private UUID secondaryPlayerId;
    private UUID teamId;
    private String description;
    private String additionalData; // JSON string
}