package com.backspacestudios.league_management.match.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backspacestudios.league_management.match.entity.PlayerStatistics;

public interface PlayerStatisticsRepository extends JpaRepository<PlayerStatistics, UUID> {
    Optional<PlayerStatistics> findByPlayerIdAndSeasonId(UUID playerId, UUID seasonId);
    List<PlayerStatistics> findBySeasonId(UUID seasonId);
}
