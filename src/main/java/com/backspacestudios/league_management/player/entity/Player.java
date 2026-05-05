package com.backspacestudios.league_management.player.entity;

import com.backspacestudios.league_management.player.enums.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "players", schema = "player")
@Data
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "player_id", columnDefinition = "UUID")
    private UUID playerId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "display_name", length = 200)
    private String displayName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "place_of_birth", length = 100)
    private String placeOfBirth;

    @Column(name = "nationality", nullable = false, length = 3)
    private String nationality;

    @Column(name = "nationality_secondary", length = 3)
    private String nationalitySecondary;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_foot", columnDefinition = "VARCHAR(10) DEFAULT 'right'")
    private PreferredFoot preferredFoot = PreferredFoot.right;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_position", nullable = false)
    private PlayerPosition primaryPosition;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "secondary_positions", columnDefinition = "JSONB")
    private Map<String, Object> secondaryPositions;

    @Column(name = "player_agent", length = 255)
    private String playerAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "international_status", columnDefinition = "VARCHAR(10) DEFAULT 'none'")
    private InternationalStatus internationalStatus = InternationalStatus.none;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'active'")
    private PlayerStatus status = PlayerStatus.active;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "JSONB")
    private Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}