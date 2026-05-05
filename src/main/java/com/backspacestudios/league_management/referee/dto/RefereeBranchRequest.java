package com.backspacestudios.league_management.referee.dto;

import java.util.UUID;

import com.backspacestudios.league_management.referee.enums.ProfessionalLevel;
import lombok.Data;

@Data
public class RefereeBranchRequest {
    private String branchName;
    private String branchCode;
    private String district;
    private ProfessionalLevel professionalLevel;
    private UUID motherBodyId; // optional
    private String contactEmail;
    private String contactPhone;
}