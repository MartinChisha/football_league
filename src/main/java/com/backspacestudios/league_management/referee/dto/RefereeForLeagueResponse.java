package com.backspacestudios.league_management.referee.dto;

import com.backspacestudios.league_management.referee.enums.RefereeClass;
import lombok.Data;

import java.util.UUID;

@Data
public class RefereeForLeagueResponse {
    private UUID refereeId;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String refereeCode;
    private RefereeClass currentClass;
    private UUID branchId;
    private String branchName;
}