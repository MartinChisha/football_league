package com.backspacestudios.league_management.player.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

import com.backspacestudios.league_management.player.enums.ContractType;

import jakarta.validation.constraints.Future;
import lombok.Data;

@Data
public class PlayerContractRequest {
    @NotNull
    private UUID playerId;

    @NotNull
    private UUID teamId;

    private ContractType contractType;

    @NotNull
    private LocalDate startDate;

    @NotNull
    @Future
    private LocalDate endDate;

    private BigDecimal salaryAmount;
    private String salaryCurrency;
    private Boolean isLoan;
    private UUID loanFromTeamId;
    private Integer squadNumber;
    private Map<String, Object> contractTerms;
}