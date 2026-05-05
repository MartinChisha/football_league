package com.backspacestudios.league_management.marketplace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bulk_pricing_tiers", schema = "marketplace")
@Getter
@Setter
public class BulkPricingTier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer minQuantity;

    private Integer maxQuantity;  // null = unlimited

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(precision = 12, scale = 2)
    private BigDecimal fixedPrice;  // if set, overrides unit price after discount

    @CreationTimestamp
    private LocalDateTime createdAt;
}