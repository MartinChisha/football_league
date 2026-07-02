package com.backspacestudios.league_management.match.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backspacestudios.league_management.match.entity.Standing;
public interface StandingRepository extends JpaRepository<Standing, UUID> {
    Optional<Standing> findBySeasonIdAndTeamId(UUID seasonId, UUID teamId);
    List<Standing> findBySeasonIdOrderByPointsDesc(UUID seasonId);
}