package com.backspacestudios.league_management.marketplace.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BulkPricingTierDTO {
    private Integer minQuantity;
    private Integer maxQuantity;  // null = unlimited
    private BigDecimal discountPercentage;
    private BigDecimal fixedPrice;  // optional
}