package com.backspacestudios.league_management.referee.dto;

import com.backspacestudios.league_management.referee.enums.RefereeClass;
import lombok.Data;

import java.util.UUID;

@Data
public class RefereeRegistrationRequestDTO {
    private UUID branchId;
    private RefereeClass requestedClass;
}