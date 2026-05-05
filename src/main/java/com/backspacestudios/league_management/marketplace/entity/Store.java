package com.backspacestudios.league_management.marketplace.entity;

import com.backspacestudios.league_management.marketplace.enums.StoreCategory;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stores", schema = "marketplace")
@Data
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID storeId;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private String storeName;

    private String storeDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreCategory storeCategory;

    private String contactEmail;
    private String contactPhone;
    private String taxId;
    private String logoImageUrl;
    private String bannerImageUrl;

    private Boolean isActive = true;

    @Column(precision = 5, scale = 2)
    private BigDecimal commissionRate = BigDecimal.valueOf(5.0);

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}