package com.backspacestudios.league_management.team.repository;

import com.backspacestudios.league_management.team.entity.Team;
import com.backspacestudios.league_management.team.enums.TeamStatus;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findByLeagueId(UUID leagueId);
    Optional<Team> findByLeagueIdAndTeamCode(UUID leagueId, String teamCode);
    boolean existsByLeagueIdAndTeamCode(UUID leagueId, String teamCode);
List<Team> findByDivisionIdAndStatus(UUID divisionId, TeamStatus status);
}