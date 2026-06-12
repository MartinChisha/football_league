package com.backspacestudios.league_management.competition.service;

import com.backspacestudios.league_management.competition.dto.*;
import com.backspacestudios.league_management.competition.entity.Fixture;
import com.backspacestudios.league_management.competition.entity.Season;
import com.backspacestudios.league_management.competition.enums.SeasonStatus;
import com.backspacestudios.league_management.competition.repository.FixtureRepository;
import com.backspacestudios.league_management.competition.repository.SeasonRepository;
import com.backspacestudios.league_management.competition.util.RoundRobinScheduler;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.league.entity.Division;
import com.backspacestudios.league_management.league.repository.DivisionRepository;
import com.backspacestudios.league_management.league.repository.LeagueAdminRepository;
import com.backspacestudios.league_management.team.entity.Team;
import com.backspacestudios.league_management.team.enums.TeamStatus;
import com.backspacestudios.league_management.team.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class SeasonService {

     private static final Logger logger = LoggerFactory.getLogger(SeasonService.class);


    private SeasonRepository seasonRepository;

    private DivisionRepository divisionRepository;

   
    private LeagueAdminRepository leagueAdminRepository;

    
    private UserRepository userRepository;

   
    private TeamRepository teamRepository;        // ← NEW

  
    private FixtureRepository fixtureRepository;  // ← NEW

    
    
    RoundRobinScheduler scheduler;
    
    
    private UUID getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"))
                .getUserId();
    }
    @Transactional(readOnly = true)
public List<SeasonResponse> getSeasonsByDivision(UUID divisionId) {
    return seasonRepository.findByDivisionId(divisionId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
}

@Transactional
public SeasonResponse updateSeasonStatus(UUID seasonId, SeasonStatus newStatus) {
    Season season = seasonRepository.findById(seasonId)
            .orElseThrow(() -> new RuntimeException("Season not found"));
    season.setStatus(newStatus);
    season = seasonRepository.save(season);
    return mapToResponse(season);
}

@Transactional(readOnly = true)
public SeasonResponse getSeasonById(UUID seasonId) {
    Season season = seasonRepository.findById(seasonId)
            .orElseThrow(() -> new RuntimeException("Season not found"));
    return mapToResponse(season);
}

@Transactional(readOnly = true)
public List<FixtureSummary> getFixturesBySeason(UUID seasonId) {
    return fixtureRepository.findBySeasonId(seasonId).stream()
            .map(this::mapToFixtureSummary)
            .collect(Collectors.toList());
}

    @Transactional
    public SeasonResponse createSeason(SeasonCreateRequest request) {
        UUID currentUserId = getCurrentUserId();

        // Check authorization: must be super admin OR league admin for the division's league
        boolean isSuperAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_super_admin"));
        if (!isSuperAdmin && !isLeagueAdminForDivision(request.getDivisionId(), currentUserId)) {
            throw new SecurityException("You are not authorized to create a season for this division");
        }

        // Validate division exists
        Division division = divisionRepository.findById(request.getDivisionId())
                .orElseThrow(() -> new RuntimeException("Division not found with ID: " + request.getDivisionId()));

        // Check duplicate season for same division and year
        if (seasonRepository.existsByDivisionIdAndSeasonYear(request.getDivisionId(), request.getYear())) {
            throw new RuntimeException("Season already exists for division " + division.getDivisionName() + " in year " + request.getYear());
        }

        // Create season entity
        Season season = new Season();
        season.setDivisionId(request.getDivisionId());
        season.setName(request.getName());
        season.setSeasonYear(request.getYear());
        season.setStartDate(request.getStartDate());
        season.setEndDate(request.getEndDate());
        season.setStatus(SeasonStatus.DRAFT);
        // Store generation type as JSON for later use
        season.setFixtureGenerationConfig("{\"type\":\"" + request.getGenerationType().name() + "\"}");

        Season saved = seasonRepository.save(season);
        logger.info("Created season {} for division {} year {}", saved.getSeasonId(), request.getDivisionId(), request.getYear());
        return mapToResponse(saved);
    }
 @Transactional
    public FixtureGenerationResponse generateFixtures(UUID seasonId, Long randomSeed, UUID userId) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new RuntimeException("Season not found"));

        if (season.getStatus() == SeasonStatus.IN_PROGRESS || season.getStatus() == SeasonStatus.COMPLETED) {
            throw new IllegalStateException("Cannot generate fixtures for season already in progress/completed");
        }

        // Authorization: league admin or super admin
        boolean isSuperAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_super_admin"));
        if (!isSuperAdmin && !isLeagueAdminForDivision(season.getDivisionId(), userId)) {
            throw new SecurityException("Only league admin can generate fixtures");
        }

        // Fetch active teams for the division using existing method
        List<Team> teams = teamRepository.findByDivisionIdAndStatus(season.getDivisionId(), TeamStatus.active);
        if (teams.size() < 2) {
            throw new IllegalStateException("Need at least 2 active teams in the division to generate fixtures");
        }

        // Determine number of legs from stored config
        int legs = parseLegsFromConfig(season.getFixtureGenerationConfig());
        Long effectiveSeed = randomSeed != null ? randomSeed : System.currentTimeMillis();

        // Generate fixtures using round‑robin scheduler
        List<Fixture> generated = scheduler.generateFixtures(seasonId, teams, legs, effectiveSeed);

        // Replace any existing fixtures for this season
        fixtureRepository.deleteBySeasonId(seasonId);
        List<Fixture> saved = fixtureRepository.saveAll(generated);

        // Update season status and config
        season.setStatus(SeasonStatus.FIXTURES_GENERATED);
        season.setFixtureGenerationConfig(updateConfigWithSeed(season.getFixtureGenerationConfig(), effectiveSeed));
        seasonRepository.save(season);

        // Build response
        List<FixtureSummary> summaries = saved.stream()
                .map(f -> new FixtureSummary(f.getMatchWeek(), f.getHomeTeamId(), f.getAwayTeamId()))
                .collect(Collectors.toList());

        logger.info("Generated {} fixtures for season {}", saved.size(), seasonId);
        return new FixtureGenerationResponse(saved.size(), effectiveSeed, summaries);
    }

    private boolean isLeagueAdminForDivision(UUID divisionId, UUID userId) {
        Division division = divisionRepository.findById(divisionId)
                .orElseThrow(() -> new RuntimeException("Division not found"));
        return leagueAdminRepository.existsByUserIdAndLeagueId(userId, division.getLeagueId());
    }
private int parseLegsFromConfig(String configJson) {
    if (configJson != null && configJson.contains("ROUND_ROBIN_DOUBLE")) return 2;
    return 1;
}

private String updateConfigWithSeed(String config, Long seed) {
    if (config == null || config.trim().isEmpty()) return "{\"randomSeed\":" + seed + "}";
    if (config.endsWith("}")) {
        String base = config.substring(0, config.length() - 1);
        if (!base.endsWith(",")) base += ",";
        return base + "\"randomSeed\":" + seed + "}";
    }
    return config;
}

    private FixtureSummary mapToFixtureSummary(Fixture fixture) {
        return new FixtureSummary(
                fixture.getMatchWeek(),
                fixture.getHomeTeamId(),
                fixture.getAwayTeamId());
    }

    private SeasonResponse mapToResponse(Season season) {
        SeasonResponse resp = new SeasonResponse();
        resp.setSeasonId(season.getSeasonId());
        resp.setDivisionId(season.getDivisionId());
        resp.setName(season.getName());
        resp.setSeasonYear(season.getSeasonYear());
        resp.setStartDate(season.getStartDate());
        resp.setEndDate(season.getEndDate());
        resp.setStatus(season.getStatus());
        resp.setFixtureGenerationConfig(season.getFixtureGenerationConfig());
        resp.setConfirmedAt(season.getConfirmedAt());
        resp.setConfirmedBy(season.getConfirmedBy());
        resp.setCreatedAt(season.getCreatedAt());
        resp.setUpdatedAt(season.getUpdatedAt());
        return resp;
    }
}