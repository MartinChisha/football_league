package com.backspacestudios.league_management.competition.repository;

import com.backspacestudios.league_management.competition.entity.Fixture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FixtureRepository extends JpaRepository<Fixture, UUID> {
    List<Fixture> findBySeasonId(UUID seasonId);
    void deleteBySeasonId(UUID seasonId);
}