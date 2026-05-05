package com.backspacestudios.league_management.marketplace.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalRequest {
    @NotNull
    private Boolean approved;
    private String reviewNotes;
}