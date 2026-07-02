package com.backspacestudios.league_management.match.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_lineups", schema = "competition")
@Getter @Setter
public class MatchLineup {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID lineupId;

    @Column(name = "fixture_id", nullable = false)
    private UUID fixtureId;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "submitted_by", nullable = false)
    private UUID submittedBy;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private String status = "draft";

    @Column(name = "starting_eleven", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)          // ✅
    private String startingEleven;

    @Column(name = "substitutes", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)          // ✅
    private String substitutes;

    @Column(name = "technical_staff", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)          // ✅
    private String technicalStaff;

    @Column(name = "version")
    private Integer version = 1;
}