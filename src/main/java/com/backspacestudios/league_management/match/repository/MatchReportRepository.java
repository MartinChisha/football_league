package com.backspacestudios.league_management.match.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backspacestudios.league_management.match.entity.MatchReport;

public interface MatchReportRepository extends JpaRepository<MatchReport, UUID> {
    Optional<MatchReport> findByFixtureId(UUID fixtureId);
}
