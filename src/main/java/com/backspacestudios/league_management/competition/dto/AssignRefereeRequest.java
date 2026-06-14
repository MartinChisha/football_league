package com.backspacestudios.league_management.competition.dto;

import com.backspacestudios.league_management.competition.enums.RefereeRole;
import lombok.Data;
import java.util.UUID;

@Data
public class AssignRefereeRequest {
    private UUID fixtureId;
    private UUID refereeId;
    private RefereeRole role;
}