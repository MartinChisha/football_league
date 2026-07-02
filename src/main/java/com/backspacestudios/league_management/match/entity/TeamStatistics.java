package com.backspacestudios.league_management.match.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "team_statistics", schema = "competition",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"team_id", "season_id"})})
@Getter @Setter
public class TeamStatistics {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID statId;
    private UUID teamId;
    private UUID seasonId;

    private int matchesPlayed, wins, draws, losses;
    private int goalsFor, goalsAgainst, goalDifference, points;
    private int homeWins, homeDraws, homeLosses;
    private int awayWins, awayDraws, awayLosses;
    private int totalShots, totalShotsOnTarget, totalCorners, totalFouls, totalOffsides;
}