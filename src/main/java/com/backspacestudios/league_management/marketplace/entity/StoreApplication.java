package com.backspacestudios.league_management.marketplace.entity;

import com.backspacestudios.league_management.marketplace.enums.ApplicationStatus;
import com.backspacestudios.league_management.marketplace.enums.StoreCategory;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "store_applications", schema = "marketplace")
@Data
public class StoreApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID applicationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String storeName;

    @Column(length = 2000)
    private String storeDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreCategory storeCategory;

    private String contactEmail;
    private String contactPhone;
    private String taxId;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.pending;

    private UUID reviewedByUserId;
    private String reviewNotes;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}