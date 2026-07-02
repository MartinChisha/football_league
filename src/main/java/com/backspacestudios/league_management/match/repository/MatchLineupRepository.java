package com.backspacestudios.league_management.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backspacestudios.league_management.match.entity.MatchLineup;

import java.util.Optional;
import java.util.UUID;
public interface MatchLineupRepository extends JpaRepository<MatchLineup, UUID> {
    Optional<MatchLineup> findByFixtureIdAndTeamId(UUID fixtureId, UUID teamId);
}

