package com.backspacestudios.league_management.marketplace.dto;

import lombok.Data;

@Data
public class StoreUpdateRequest {
    private String storeName;
    private String storeDescription;
    private String contactEmail;
    private String contactPhone;
    private String logoImageUrl;
    private String bannerImageUrl;
}