package com.backspacestudios.league_management.competition.dto;

import lombok.Data;
import java.util.UUID;

import com.backspacestudios.league_management.competition.entity.RefereeRole;

@Data
public class AssignRefereeRequest {
    private UUID fixtureId;
    private UUID refereeId;
    private RefereeRole role;
}