package com.backspacestudios.league_management.marketplace.dto;

import com.backspacestudios.league_management.marketplace.enums.StoreCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class StoreResponse {
    private UUID storeId;
    private UUID userId;
    private String storeName;
    private String storeDescription;
    private StoreCategory storeCategory;
    private String contactEmail;
    private String contactPhone;
    private String logoImageUrl;
    private String bannerImageUrl;
    private Boolean isActive;
    private BigDecimal commissionRate;
    private LocalDateTime createdAt;
}