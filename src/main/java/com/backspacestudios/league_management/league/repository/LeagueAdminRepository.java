package com.backspacestudios.league_management.league.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.backspacestudios.league_management.league.entity.LeagueAdmin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface LeagueAdminRepository extends JpaRepository<LeagueAdmin, UUID> {
    Optional<LeagueAdmin> findByUserIdAndLeagueId(UUID userId, UUID leagueId);
    List<LeagueAdmin> findByLeagueId(UUID leagueId);
    List<LeagueAdmin> findByUserId(UUID userId);
    boolean existsByUserIdAndLeagueId(UUID userId, UUID leagueId);
}