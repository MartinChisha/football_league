package com.backspacestudios.league_management.league.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backspacestudios.league_management.league.entity.League;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeagueRepository extends JpaRepository<League, UUID> {
    Optional<League> findByLeagueCode(String leagueCode);
    boolean existsByLeagueCode(String leagueCode);
}
