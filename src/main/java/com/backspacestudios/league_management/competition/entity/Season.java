package com.backspacestudios.league_management.competition.entity;
import com.backspacestudios.league_management.competition.enums.SeasonStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "seasons", schema = "competition")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID seasonId;

    @Column(name = "division_id", nullable = false)
    private UUID divisionId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "season_year", nullable = false)
    private Integer seasonYear;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SeasonStatus status = SeasonStatus.DRAFT;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "fixture_generation_config", columnDefinition = "jsonb")
    private String fixtureGenerationConfig; // store as JSON string

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "confirmed_by")
    private UUID confirmedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
