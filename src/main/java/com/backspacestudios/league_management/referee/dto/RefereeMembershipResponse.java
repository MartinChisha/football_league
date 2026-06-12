package com.backspacestudios.league_management.referee.dto;

import com.backspacestudios.league_management.referee.enums.RefereeClass;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RefereeMembershipResponse {
    private UUID refereeId;
    private UUID userId;
    private String refereeCode;
    private RefereeClass currentClass;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private String nationality;
    private UUID branchId;
    private LocalDate joinedDate;
    private String membershipStatus;
    private String certificateUrl;
}