package com.backspacestudios.league_management.marketplace.dto;

import com.backspacestudios.league_management.marketplace.enums.ProductCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProductResponse {
    private UUID productId;
    private UUID storeId;
    private String name;
    private String description;
    private ProductCategory category;
    private List<String> tags;
    private BigDecimal price;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private BigDecimal weightKg;
    private Boolean isAvailable;
    private Map<String, Object> metadata;
    private List<ProductImageResponse> images;
    private List<BulkPricingTierDTO> bulkPricingTiers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}