package com.backspacestudios.league_management.match.controller;

import com.backspacestudios.league_management.core.service.UserService;
import com.backspacestudios.league_management.match.dto.FixtureLineupsResponse;
import com.backspacestudios.league_management.match.dto.LineupRequest;
import com.backspacestudios.league_management.match.dto.LineupResponse;
import com.backspacestudios.league_management.match.service.LineupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lineups")
@RequiredArgsConstructor
public class LineupController {

    private final LineupService lineupService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<LineupResponse> getLineup(@RequestParam UUID fixtureId,
                                                    @RequestParam UUID teamId) {
        return ResponseEntity.ok(lineupService.getOrCreate(fixtureId, teamId));
    }

    @GetMapping("/fixture/{fixtureId}")
    public ResponseEntity<FixtureLineupsResponse> getFixtureLineups(@PathVariable UUID fixtureId) {
        return ResponseEntity.ok(lineupService.getFixtureLineups(fixtureId));
    }

    @PostMapping
    @PreAuthorize("hasRole('team_manager')")
    public ResponseEntity<LineupResponse> saveLineup(@Valid @RequestBody LineupRequest request) {
        UUID userId = userService.getCurrentUser().getUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(lineupService.saveDraft(userId, request));
    }

    @PutMapping("/{lineupId}/submit")
    @PreAuthorize("hasRole('team_manager')")
    public ResponseEntity<LineupResponse> submitLineup(@PathVariable UUID lineupId) {
        return ResponseEntity.ok(lineupService.submit(lineupId));
    }
}