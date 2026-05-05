package com.backspacestudios.league_management.player.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.backspacestudios.league_management.player.enums.ContractStatus;
import com.backspacestudios.league_management.player.enums.ContractType;
import com.backspacestudios.league_management.player.enums.RegistrationStatus;

import lombok.Data;

@Data
public class PlayerContractResponse {
    private UUID contractId;
    private UUID playerId;
    private String playerFullName; // joined from player entity
    private UUID teamId;
    private String teamName;
    private ContractType contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal salaryAmount;
    private String salaryCurrency;
    private ContractStatus contractStatus;
    private Boolean isLoan;
    private UUID loanFromTeamId;
    private RegistrationStatus registrationStatus;
    private Integer squadNumber;
    private Map<String, Object> contractTerms;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
}