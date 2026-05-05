package com.backspacestudios.league_management.player.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.backspacestudios.league_management.player.enums.InternationalStatus;
import com.backspacestudios.league_management.player.enums.PlayerPosition;
import com.backspacestudios.league_management.player.enums.PlayerStatus;
import com.backspacestudios.league_management.player.enums.PreferredFoot;

import lombok.Data;

@Data
public class PlayerResponse {
    private UUID playerId;
    private String firstName;
    private String lastName;
    private String displayName;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String nationality;
    private String nationalitySecondary;
    private PreferredFoot preferredFoot;
    private PlayerPosition primaryPosition;
    private Map<String, Object> secondaryPositions;
    private String playerAgent;
    private InternationalStatus internationalStatus;
    private PlayerStatus status;
    private String profileImageUrl;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}