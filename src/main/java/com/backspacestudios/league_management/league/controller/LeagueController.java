package com.backspacestudios.league_management.league.controller;

import com.backspacestudios.league_management.league.dto.LeagueRequest;
import com.backspacestudios.league_management.league.dto.LeagueResponse;
import com.backspacestudios.league_management.league.service.LeagueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leagues")
@PreAuthorize("hasRole('super_admin')")  // Only super admin can manage leagues
public class LeagueController {

    private final LeagueService leagueService;

    LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }
    public ResponseEntity<LeagueResponse> createLeague(@Valid @RequestBody LeagueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leagueService.createLeague(request));
    }

    @GetMapping
    public ResponseEntity<List<LeagueResponse>> getAllLeagues() {
        return ResponseEntity.ok(leagueService.getAllLeagues());
    }

    @GetMapping("/{leagueId}")
    public ResponseEntity<LeagueResponse> getLeagueById(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(leagueService.getLeagueById(leagueId));
    }

    @PutMapping("/update/{leagueId}")
    public ResponseEntity<LeagueResponse> updateLeague(@PathVariable UUID leagueId,
                                                       @Valid @RequestBody LeagueRequest request) {
        return ResponseEntity.ok(leagueService.updateLeague(leagueId, request));
    }

    @DeleteMapping("/{leagueId}")
    public ResponseEntity<Void> deleteLeague(@PathVariable UUID leagueId) {
        leagueService.deleteLeague(leagueId);
        return ResponseEntity.noContent().build();
    }
}