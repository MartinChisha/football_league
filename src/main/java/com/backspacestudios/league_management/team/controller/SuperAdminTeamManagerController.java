package com.backspacestudios.league_management.team.controller;

import com.backspacestudios.league_management.team.dto.TeamManagerResponse;
import com.backspacestudios.league_management.team.service.TeamManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/team-managers")
@PreAuthorize("hasRole('super_admin')")
public class SuperAdminTeamManagerController {
    private final TeamManagerService teamManagerService;

    SuperAdminTeamManagerController(TeamManagerService teamManagerService) {
        this.teamManagerService = teamManagerService;
    }

    @GetMapping
    public ResponseEntity<List<TeamManagerResponse>> getAllTeamManagers() {
        return ResponseEntity.ok(teamManagerService.getAllTeamManagers());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TeamManagerResponse>> getPendingTeamManagers() {
        return ResponseEntity.ok(teamManagerService.getPendingTeamManagers());
    }
}