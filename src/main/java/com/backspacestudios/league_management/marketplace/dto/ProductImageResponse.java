package com.backspacestudios.league_management.marketplace.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ProductImageResponse {
    private UUID imageId;
    private String imageUrl;
    private Integer displayOrder;
}