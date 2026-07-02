package com.backspacestudios.league_management.match.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backspacestudios.league_management.match.entity.TeamStatistics;

public interface TeamStatisticsRepository extends JpaRepository<TeamStatistics, UUID> {
    Optional<TeamStatistics> findByTeamIdAndSeasonId(UUID teamId, UUID seasonId);
    List<TeamStatistics> findBySeasonId(UUID seasonId);
}
