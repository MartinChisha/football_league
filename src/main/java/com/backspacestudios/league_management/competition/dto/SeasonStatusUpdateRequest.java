package com.backspacestudios.league_management.competition.dto;

import com.backspacestudios.league_management.competition.enums.SeasonStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SeasonStatusUpdateRequest {
    @NotNull
    private SeasonStatus status;
}