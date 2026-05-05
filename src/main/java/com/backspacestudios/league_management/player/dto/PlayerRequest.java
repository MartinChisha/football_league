package com.backspacestudios.league_management.player.dto;

import java.time.LocalDate;
import java.util.Map;

import org.antlr.v4.runtime.misc.NotNull;

import com.backspacestudios.league_management.player.enums.InternationalStatus;
import com.backspacestudios.league_management.player.enums.PlayerPosition;
import com.backspacestudios.league_management.player.enums.PlayerStatus;
import com.backspacestudios.league_management.player.enums.PreferredFoot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

@Data
public class PlayerRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String displayName;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    private String placeOfBirth;

    @NotBlank
    private String nationality; // 3-letter code

    private String nationalitySecondary;

    private PreferredFoot preferredFoot;

    @NotNull
    private PlayerPosition primaryPosition;

    private Map<String, Object> secondaryPositions;

    private String playerAgent;

    private InternationalStatus internationalStatus;

    private PlayerStatus status;

    private String profileImageUrl;

    private Map<String, Object> metadata;
}