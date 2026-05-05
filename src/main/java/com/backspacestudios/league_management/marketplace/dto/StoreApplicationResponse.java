package com.backspacestudios.league_management.marketplace.dto;

import com.backspacestudios.league_management.marketplace.enums.ApplicationStatus;
import com.backspacestudios.league_management.marketplace.enums.StoreCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class StoreApplicationResponse {
    private UUID applicationId;
    private UUID userId;
    private String storeName;
    private String storeDescription;
    private StoreCategory storeCategory;
    private String contactEmail;
    private String contactPhone;
    private String taxId;
    private ApplicationStatus status;
    private String reviewNotes;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime createdAt;
}