package com.backspacestudios.league_management.competition.controller;

import com.backspacestudios.league_management.competition.dto.FixtureGenerationRequest;
import com.backspacestudios.league_management.competition.dto.FixtureGenerationResponse;
import com.backspacestudios.league_management.competition.dto.FixtureSummary;
import com.backspacestudios.league_management.competition.dto.SeasonCreateRequest;
import com.backspacestudios.league_management.competition.dto.SeasonResponse;
import com.backspacestudios.league_management.competition.dto.SeasonStatusUpdateRequest;
import com.backspacestudios.league_management.competition.service.SeasonService;
import com.backspacestudios.league_management.core.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing seasons and their fixtures.
 * All endpoints require authentication. Role-based access is enforced via @PreAuthorize.
 */
@RestController
@RequestMapping("/api/seasons")
public class SeasonController {

    private static final Logger log = LoggerFactory.getLogger(SeasonController.class);

    @Autowired
    private SeasonService seasonService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Creates a new season for a given division.
     * Only super admins or league admins (of the division's league) may create a season.
     *
     * @param request the season creation payload (division ID, name, year, dates, generation type)
     * @return the created season with HTTP 201 (Created)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('super_admin', 'league_admin')")
    public ResponseEntity<SeasonResponse> createSeason(@Valid @RequestBody SeasonCreateRequest request) {
        log.info("Received request to create season for division: {}", request.getDivisionId());
        SeasonResponse response = seasonService.createSeason(request);
        log.info("Season created successfully with ID: {}", response.getSeasonId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all seasons belonging to a specific division.
     * Accessible to super admins, league admins, team managers, and referees (read-only).
     *
     * @param divisionId the ID of the division
     * @return list of seasons (may be empty)
     */
    @GetMapping("/division/{divisionId}")
    @PreAuthorize("hasAnyRole('super_admin', 'league_admin', 'team_manager', 'referee')")
    public ResponseEntity<List<SeasonResponse>> getSeasonsByDivision(@PathVariable UUID divisionId) {
        log.debug("Fetching seasons for division: {}", divisionId);
        List<SeasonResponse> seasons = seasonService.getSeasonsByDivision(divisionId);
        return ResponseEntity.ok(seasons);
    }

    /**
     * Generates (or regenerates) fixtures for a season using a round‑robin algorithm.
     * - If the season is already in progress or completed, generation is not allowed.
     * - Only league admins (of the division's league) or super admins may generate fixtures.
     * - An optional random seed can be provided for deterministic shuffling of team order.
     * - Existing fixtures for the season are deleted and replaced.
     *
     * @param seasonId  the ID of the season
     * @param request   optional request body containing a custom random seed (null → system-generated seed)
     * @return details of the generated fixtures (count, used seed, and a summary list)
     */
    @PostMapping("/{seasonId}/fixtures/generate")
    @PreAuthorize("hasAnyRole('super_admin', 'league_admin')")
    public ResponseEntity<FixtureGenerationResponse> generateFixtures(
            @PathVariable UUID seasonId,
            @RequestBody(required = false) @Valid FixtureGenerationRequest request) {
        
        UUID currentUserId = getCurrentUserId();
        Long seed = (request != null) ? request.getRandomSeed() : null;
        
        log.info("User {} requesting fixture generation for season {} with seed: {}", 
                 currentUserId, seasonId, seed);
        
        FixtureGenerationResponse response = seasonService.generateFixtures(seasonId, seed, currentUserId);
        
        log.info("Successfully generated {} fixtures for season {}", response.getTotalMatches(), seasonId);
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to retrieve the currently authenticated user's ID from the security context.
     * Throws a runtime exception if the user cannot be found (should never happen with valid JWT).
     *
     * @return the UUID of the authenticated user
     */
    private UUID getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"))
                .getUserId();
    }
    @PutMapping("/{seasonId}/status")
@PreAuthorize("hasAnyRole('league_admin','super_admin')")
public ResponseEntity<SeasonResponse> updateSeasonStatus(
        @PathVariable UUID seasonId,
        @Valid @RequestBody SeasonStatusUpdateRequest request) {
    return ResponseEntity.ok(seasonService.updateSeasonStatus(seasonId, request.getStatus()));
}

/**
 * Retrieves a single season by its ID.
 */
@GetMapping("/{seasonId}")
@PreAuthorize("hasAnyRole('super_admin', 'league_admin', 'team_manager', 'referee')")
public ResponseEntity<SeasonResponse> getSeasonById(@PathVariable UUID seasonId) {
    log.debug("Fetching season by ID: {}", seasonId);
    SeasonResponse season = seasonService.getSeasonById(seasonId);
    return ResponseEntity.ok(season);
}

/**
 * Retrieves all fixtures for a given season.
 */
@GetMapping("/{seasonId}/fixtures")
@PreAuthorize("hasAnyRole('super_admin', 'league_admin', 'team_manager', 'referee')")
public ResponseEntity<List<FixtureSummary>> getFixturesBySeason(@PathVariable UUID seasonId) {
    log.debug("Fetching fixtures for season: {}", seasonId);
    List<FixtureSummary> fixtures = seasonService.getFixturesBySeason(seasonId);
    return ResponseEntity.ok(fixtures);
}

}