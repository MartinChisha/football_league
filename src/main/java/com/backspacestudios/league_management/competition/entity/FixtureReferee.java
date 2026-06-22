package com.backspacestudios.league_management.competition.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fixture_referees", schema = "competition")
@Data
public class FixtureReferee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID assignmentId;

    @Column(name = "fixture_id", nullable = false)
    private UUID fixtureId;

    @Column(name = "referee_id", nullable = false)
    private UUID refereeId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RefereeRole role;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "assigned_by")
    private UUID assignedBy;

    @Column(name = "is_notified")
    private Boolean isNotified = false;
}