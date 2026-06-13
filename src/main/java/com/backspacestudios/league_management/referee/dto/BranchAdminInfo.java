package com.backspacestudios.league_management.referee.dto;

import com.backspacestudios.league_management.referee.enums.ProfessionalLevel;
import lombok.Data;
import java.util.UUID;

@Data
public class BranchAdminInfo {
    private UUID branchId;
    private String branchName;
    private String branchCode;
    private String district;
    private ProfessionalLevel professionalLevel;
    private String contactEmail;
    private String contactPhone;
}