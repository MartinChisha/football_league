package com.backspacestudios.league_management.competition.entity;
import com.backspacestudios.league_management.competition.enums.FixtureStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "fixtures", schema = "competition")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fixture {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID fixtureId;

    @Column(name = "season_id", nullable = false)
    private UUID seasonId;

    @Column(name = "match_week", nullable = false)
    private Integer matchWeek;

    @Column(name = "home_team_id", nullable = false)
    private UUID homeTeamId;

    @Column(name = "away_team_id", nullable = false)
    private UUID awayTeamId;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    private String venue;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FixtureStatus status = FixtureStatus.SCHEDULED;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "match_report", columnDefinition = "TEXT")
    private String matchReport;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}