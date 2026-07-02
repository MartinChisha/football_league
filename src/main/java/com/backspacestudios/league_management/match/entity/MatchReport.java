package com.backspacestudios.league_management.match.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "match_reports", schema = "competition")
@Getter @Setter
public class MatchReport {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reportId;
    private UUID fixtureId;
    private UUID refereeId;
    private int homeScore;
    private int awayScore;
    private String reportStatus = "draft";  // draft, submitted, verified

    private LocalDateTime matchStartTime;
    private LocalDateTime matchEndTime;
    private Integer attendance;
    private String weatherConditions;

    private Integer homePossession, awayPossession;
    private Integer homeShots, awayShots;
    private Integer homeShotsOnTarget, awayShotsOnTarget;
    private Integer homeFouls, awayFouls;
    private Integer homeCorners, awayCorners;
    private Integer homeOffsides, awayOffsides;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy, updatedBy;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchEvent> events = new ArrayList<>();
}