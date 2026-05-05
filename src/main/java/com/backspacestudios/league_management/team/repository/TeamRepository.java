package com.backspacestudios.league_management.team.repository;

import com.backspacestudios.league_management.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findByLeagueId(UUID leagueId);
    Optional<Team> findByLeagueIdAndTeamCode(UUID leagueId, String teamCode);
    boolean existsByLeagueIdAndTeamCode(UUID leagueId, String teamCode);
}