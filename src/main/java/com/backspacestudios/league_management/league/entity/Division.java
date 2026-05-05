package com.backspacestudios.league_management.league.entity;

import com.backspacestudios.league_management.league.enums.DivisionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "divisions", schema = "league")
@Data
@NoArgsConstructor
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "division_id", columnDefinition = "UUID")
    private UUID divisionId;

    @Column(name = "league_id", nullable = false, columnDefinition = "UUID")
    private UUID leagueId;

    @Column(name = "parent_division_id", columnDefinition = "UUID")
    private UUID parentDivisionId;

    @Column(name = "division_name", nullable = false, length = 255)
    private String divisionName;

    @Column(name = "division_code", nullable = false, length = 50)
    private String divisionCode;

    @Column(name = "division_level")
    private Integer divisionLevel = 1;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "promotion_spots")
    private Integer promotionSpots = 0;

    @Column(name = "relegation_spots")
    private Integer relegationSpots = 0;

    @Column(name = "max_teams")
    private Integer maxTeams = 20;

    @Column(name = "min_teams")
    private Integer minTeams = 10;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sorting_rules", columnDefinition = "JSONB")
    private Map<String, Object> sortingRules;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'pending'")
    private DivisionStatus status = DivisionStatus.pending;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "division_config", columnDefinition = "JSONB")
    private Map<String, Object> divisionConfig;

    @Column(name = "created_by", columnDefinition = "UUID")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}