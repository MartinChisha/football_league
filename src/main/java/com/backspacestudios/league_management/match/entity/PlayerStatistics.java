package com.backspacestudios.league_management.match.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "player_statistics", schema = "competition",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"player_id", "season_id"})})
@Getter @Setter
public class PlayerStatistics {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID statId;
    private UUID playerId;
    private UUID seasonId;

    private int appearances, starts, minutesPlayed;
    private int goals, assists, yellowCards, redCards;
    private int shots, shotsOnTarget, keyPasses, tackles, interceptions, clearances, dribblesSuccessful;
    private int foulsCommitted, foulsDrawn, offsides;

    @Column(precision = 5, scale = 2) private BigDecimal goalsPer90;
    @Column(precision = 5, scale = 2) private BigDecimal assistsPer90;
    @Column(precision = 5, scale = 2) private BigDecimal passAccuracy;
    @Column(precision = 3, scale = 1) private BigDecimal rating;
}