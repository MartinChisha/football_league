package com.backspacestudios.league_management.match.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "standings", schema = "competition",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"season_id", "team_id"})})
@Getter @Setter
public class Standing {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID standingId;
    private UUID seasonId;
    private UUID teamId;

    private int played, wins, draws, losses;
    private int goalsFor, goalsAgainst, goalDifference, points;
    private int winsHome, drawsHome, lossesHome, goalsForHome, goalsAgainstHome;
    private int winsAway, drawsAway, lossesAway, goalsForAway, goalsAgainstAway;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}