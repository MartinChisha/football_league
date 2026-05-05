package com.backspacestudios.league_management.marketplace.dto;

import com.backspacestudios.league_management.marketplace.enums.StoreCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreApplicationRequest {
    @NotBlank
    private String storeName;

    private String storeDescription;

    @NotNull
    private StoreCategory storeCategory;

    private String contactEmail;
    private String contactPhone;
    private String taxId;
}