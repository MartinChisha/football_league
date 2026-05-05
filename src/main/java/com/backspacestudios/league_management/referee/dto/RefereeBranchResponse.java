package com.backspacestudios.league_management.referee.dto;

import com.backspacestudios.league_management.referee.enums.ProfessionalLevel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RefereeBranchResponse {
    private UUID branchId;
    private String branchName;
    private String branchCode;
    private String district;
    private ProfessionalLevel professionalLevel;
    private UUID motherBodyId;
    private String contactEmail;
    private String contactPhone;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}