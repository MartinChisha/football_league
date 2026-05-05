package com.backspacestudios.league_management.marketplace.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStockRequest {
    @NotNull
    private Integer delta;  // positive to add, negative to reduce
}