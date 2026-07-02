package com.backspacestudios.league_management.match.controller;

import com.backspacestudios.league_management.match.dto.StandingResponse;
import com.backspacestudios.league_management.match.entity.Standing;
import com.backspacestudios.league_management.match.repository.StandingRepository;
import com.backspacestudios.league_management.team.entity.Team;
import com.backspacestudios.league_management.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/standings")
@RequiredArgsConstructor
public class StandingController {

    private final StandingRepository standingRepo;
    private final TeamRepository teamRepo;

    @GetMapping("/{seasonId}")
    public ResponseEntity<List<StandingResponse>> getStandings(@PathVariable UUID seasonId) {
        List<Standing> standings = standingRepo.findBySeasonIdOrderByPointsDesc(seasonId);
        List<StandingResponse> response = standings.stream().map(s -> {
            Team team = teamRepo.findById(s.getTeamId()).orElse(null);
            return StandingResponse.builder()
                    .standingId(s.getStandingId())
                    .seasonId(s.getSeasonId())
                    .teamId(s.getTeamId())
                    .teamName(team != null ? team.getTeamName() : "Unknown")
                    .played(s.getPlayed())
                    .wins(s.getWins())
                    .draws(s.getDraws())
                    .losses(s.getLosses())
                    .goalsFor(s.getGoalsFor())
                    .goalsAgainst(s.getGoalsAgainst())
                    .goalDifference(s.getGoalDifference())
                    .points(s.getPoints())
                    .winsHome(s.getWinsHome())
                    .drawsHome(s.getDrawsHome())
                    .lossesHome(s.getLossesHome())
                    .goalsForHome(s.getGoalsForHome())
                    .goalsAgainstHome(s.getGoalsAgainstHome())
                    .winsAway(s.getWinsAway())
                    .drawsAway(s.getDrawsAway())
                    .lossesAway(s.getLossesAway())
                    .goalsForAway(s.getGoalsForAway())
                    .goalsAgainstAway(s.getGoalsAgainstAway())
                    .build();
        }).toList();
        return ResponseEntity.ok(response);
    }
}