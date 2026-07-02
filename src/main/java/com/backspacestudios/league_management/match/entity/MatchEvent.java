package com.backspacestudios.league_management.match.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_events", schema = "competition")
@Getter @Setter
public class MatchEvent {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private MatchReport report;

    private String eventType;
    private int minute;
    private UUID playerId;
    private UUID secondaryPlayerId;
    private UUID teamId;
    private String description;

    @Column(name = "additional_data", columnDefinition = "jsonb")
    private String additionalData;

    private LocalDateTime createdAt = LocalDateTime.now();
}