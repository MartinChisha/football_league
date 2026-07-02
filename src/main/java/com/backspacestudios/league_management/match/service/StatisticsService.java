package com.backspacestudios.league_management.match.service;

import com.backspacestudios.league_management.competition.entity.Fixture;
import com.backspacestudios.league_management.competition.repository.FixtureRepository;
import com.backspacestudios.league_management.match.entity.*;
import com.backspacestudios.league_management.match.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final MatchReportRepository reportRepository;
    private final MatchEventRepository eventRepository;
    private final PlayerStatisticsRepository playerStatsRepo;
    private final TeamStatisticsRepository teamStatsRepo;
    private final StandingRepository standingRepo;
    private final FixtureRepository fixtureRepository;

    /**
     * Recalculates all statistics for the season that the given fixture belongs to.
     * Call this after a match report has been verified.
     */
    @Transactional
    public void updateSeasonStats(UUID fixtureId) {
        Fixture fixture = fixtureRepository.findById(fixtureId)
                .orElseThrow(() -> new RuntimeException("Fixture not found"));
        UUID seasonId = fixture.getSeasonId();
        MatchReport report = reportRepository.findByFixtureId(fixtureId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        if (!"verified".equals(report.getReportStatus())) {
            log.warn("Report {} is not verified, skipping stats update", fixtureId);
            return;
        }

        UUID homeTeamId = fixture.getHomeTeamId();
        UUID awayTeamId = fixture.getAwayTeamId();

        // 1. Update player statistics from events
        List<MatchEvent> events = eventRepository.findByReportReportId(report.getReportId());
        for (MatchEvent event : events) {
            updatePlayerStats(event, seasonId);
        }

        // 2. Update team statistics and standings
        updateTeamStats(homeTeamId, awayTeamId, report, seasonId);
    }

    private void updatePlayerStats(MatchEvent event, UUID seasonId) {
        if (event.getPlayerId() == null) return;
        PlayerStatistics ps = playerStatsRepo
                .findByPlayerIdAndSeasonId(event.getPlayerId(), seasonId)
                .orElseGet(() -> {
                    PlayerStatistics p = new PlayerStatistics();
                    p.setPlayerId(event.getPlayerId());
                    p.setSeasonId(seasonId);
                    return p;
                });

        switch (event.getEventType().toLowerCase()) {
            case "goal":          ps.setGoals(ps.getGoals() + 1); break;
            case "assist":        ps.setAssists(ps.getAssists() + 1); break;
            case "yellow_card":   ps.setYellowCards(ps.getYellowCards() + 1); break;
            case "red_card":      ps.setRedCards(ps.getRedCards() + 1); break;
            // additional types can be added here
        }
        playerStatsRepo.save(ps);
    }

    private void updateTeamStats(UUID homeTeamId, UUID awayTeamId,
                                 MatchReport report, UUID seasonId) {
        TeamStatistics homeStats = getOrCreateTeamStats(homeTeamId, seasonId);
        TeamStatistics awayStats = getOrCreateTeamStats(awayTeamId, seasonId);

        boolean homeWin = report.getHomeScore() > report.getAwayScore();
        boolean draw = report.getHomeScore() == report.getAwayScore();

        // Matches played
        homeStats.setMatchesPlayed(homeStats.getMatchesPlayed() + 1);
        awayStats.setMatchesPlayed(awayStats.getMatchesPlayed() + 1);

        // Goals
        homeStats.setGoalsFor(homeStats.getGoalsFor() + report.getHomeScore());
        homeStats.setGoalsAgainst(homeStats.getGoalsAgainst() + report.getAwayScore());
        awayStats.setGoalsFor(awayStats.getGoalsFor() + report.getAwayScore());
        awayStats.setGoalsAgainst(awayStats.getGoalsAgainst() + report.getHomeScore());

        // Win / draw / loss
        if (homeWin) {
            homeStats.setWins(homeStats.getWins() + 1);
            homeStats.setHomeWins(homeStats.getHomeWins() + 1);
            awayStats.setLosses(awayStats.getLosses() + 1);
            awayStats.setAwayLosses(awayStats.getAwayLosses() + 1);
            homeStats.setPoints(homeStats.getPoints() + 3);
        } else if (draw) {
            homeStats.setDraws(homeStats.getDraws() + 1);
            homeStats.setHomeDraws(homeStats.getHomeDraws() + 1);
            awayStats.setDraws(awayStats.getDraws() + 1);
            awayStats.setAwayDraws(awayStats.getAwayDraws() + 1);
            homeStats.setPoints(homeStats.getPoints() + 1);
            awayStats.setPoints(awayStats.getPoints() + 1);
        } else {
            awayStats.setWins(awayStats.getWins() + 1);
            awayStats.setAwayWins(awayStats.getAwayWins() + 1);
            homeStats.setLosses(homeStats.getLosses() + 1);
            homeStats.setHomeLosses(homeStats.getHomeLosses() + 1);
            awayStats.setPoints(awayStats.getPoints() + 3);
        }

        // Aggregate stats (shots, fouls, etc.)
        homeStats.setTotalShots(homeStats.getTotalShots() + nvl(report.getHomeShots()));
        awayStats.setTotalShots(awayStats.getTotalShots() + nvl(report.getAwayShots()));
        homeStats.setTotalShotsOnTarget(homeStats.getTotalShotsOnTarget() + nvl(report.getHomeShotsOnTarget()));
        awayStats.setTotalShotsOnTarget(awayStats.getTotalShotsOnTarget() + nvl(report.getAwayShotsOnTarget()));
        homeStats.setTotalFouls(homeStats.getTotalFouls() + nvl(report.getHomeFouls()));
        awayStats.setTotalFouls(awayStats.getTotalFouls() + nvl(report.getAwayFouls()));
        homeStats.setTotalCorners(homeStats.getTotalCorners() + nvl(report.getHomeCorners()));
        awayStats.setTotalCorners(awayStats.getTotalCorners() + nvl(report.getAwayCorners()));
        homeStats.setTotalOffsides(homeStats.getTotalOffsides() + nvl(report.getHomeOffsides()));
        awayStats.setTotalOffsides(awayStats.getTotalOffsides() + nvl(report.getAwayOffsides()));

        // Goal difference
        homeStats.setGoalDifference(homeStats.getGoalsFor() - homeStats.getGoalsAgainst());
        awayStats.setGoalDifference(awayStats.getGoalsFor() - awayStats.getGoalsAgainst());

        teamStatsRepo.save(homeStats);
        teamStatsRepo.save(awayStats);

        // Standings
        updateStanding(seasonId, homeTeamId, homeStats);
        updateStanding(seasonId, awayTeamId, awayStats);
    }

    private void updateStanding(UUID seasonId, UUID teamId, TeamStatistics stats) {
        Standing s = standingRepo.findBySeasonIdAndTeamId(seasonId, teamId)
                .orElseGet(() -> {
                    Standing st = new Standing();
                    st.setSeasonId(seasonId);
                    st.setTeamId(teamId);
                    return st;
                });
        s.setPlayed(stats.getMatchesPlayed());
        s.setWins(stats.getWins());
        s.setDraws(stats.getDraws());
        s.setLosses(stats.getLosses());
        s.setGoalsFor(stats.getGoalsFor());
        s.setGoalsAgainst(stats.getGoalsAgainst());
        s.setGoalDifference(stats.getGoalDifference());
        s.setPoints(stats.getPoints());
        s.setWinsHome(stats.getHomeWins());
        s.setDrawsHome(stats.getHomeDraws());
        s.setLossesHome(stats.getHomeLosses());
        s.setGoalsForHome(stats.getGoalsFor() - stats.getGoalsAgainst()); // simple approximation – you can store actual home/away stats if needed
        s.setGoalsAgainstHome(0); // placeholder – you can enrich later
        s.setWinsAway(stats.getAwayWins());
        s.setDrawsAway(stats.getAwayDraws());
        s.setLossesAway(stats.getAwayLosses());
        s.setGoalsForAway(0); // placeholder
        s.setGoalsAgainstAway(0);
        standingRepo.save(s);
    }

    private TeamStatistics getOrCreateTeamStats(UUID teamId, UUID seasonId) {
        return teamStatsRepo.findByTeamIdAndSeasonId(teamId, seasonId)
                .orElseGet(() -> {
                    TeamStatistics ts = new TeamStatistics();
                    ts.setTeamId(teamId);
                    ts.setSeasonId(seasonId);
                    return ts;
                });
    }

    private int nvl(Integer value) {
        return value != null ? value : 0;
    }
}