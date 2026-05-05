package com.backspacestudios.league_management.marketplace.dto;

import com.backspacestudios.league_management.marketplace.enums.ProductCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProductRequest {
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    private ProductCategory category;
    
    private List<String> tags;
    
    @NotNull
    @PositiveOrZero
    private BigDecimal price;
    
    @Min(0)
    private Integer stockQuantity = 0;
    
    private Integer lowStockThreshold = 5;
    
    private BigDecimal weightKg;
    
    private Boolean isAvailable = true;
    
    private Map<String, Object> metadata;
    
    private List<BulkPricingTierDTO> bulkPricingTiers;
}