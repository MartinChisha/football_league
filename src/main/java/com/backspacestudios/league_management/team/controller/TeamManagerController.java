package com.backspacestudios.league_management.team.controller;

import com.backspacestudios.league_management.team.dto.TeamManagerApprovalRequest;
import com.backspacestudios.league_management.team.dto.TeamManagerRequest;
import com.backspacestudios.league_management.team.dto.TeamManagerResponse;
import com.backspacestudios.league_management.team.dto.TeamResponse;
import com.backspacestudios.league_management.team.service.TeamManagerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/team-managers")
public class TeamManagerController {
    private final TeamManagerService teamManagerService;

    TeamManagerController(TeamManagerService teamManagerService) {
        this.teamManagerService = teamManagerService;
    }

    // League admin submits a request
    @PostMapping("/requests")
    @PreAuthorize("hasRole('league_admin')")
    public ResponseEntity<TeamManagerResponse> requestTeamManager(@Valid @RequestBody TeamManagerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamManagerService.requestTeamManager(request));
    }

    // League admin views all team managers for their league (needs leagueId param)
    @GetMapping("/league/{leagueId}")
    @PreAuthorize("hasRole('league_admin')")
    public ResponseEntity<List<TeamManagerResponse>> getTeamManagersByLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(teamManagerService.getTeamManagersByLeague(leagueId));
    }

    // Super admin approves or rejects
    @PutMapping("/approvals")
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<TeamManagerResponse> approveTeamManager(@Valid @RequestBody TeamManagerApprovalRequest request) {
        return ResponseEntity.ok(teamManagerService.approveTeamManager(request));
    }

    // Team manager views their own team details
    @GetMapping("/my-team")
    @PreAuthorize("hasRole('team_manager')")
    public ResponseEntity<TeamResponse> getMyTeam() {
        return ResponseEntity.ok(teamManagerService.getMyTeam());
    }
}