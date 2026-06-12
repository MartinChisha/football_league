package com.backspacestudios.league_management.league.controller;

import com.backspacestudios.league_management.league.dto.LeagueResponse;
import com.backspacestudios.league_management.league.service.LeagueAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/league-admin")
@PreAuthorize("hasRole('league_admin')")
public class LeagueAdminController {

    private final LeagueAdminService leagueAdminService;

    LeagueAdminController(LeagueAdminService leagueAdminService) {
        this.leagueAdminService = leagueAdminService;
    }

    @GetMapping("/league")
    public ResponseEntity<LeagueResponse> getMyLeague() {
        return ResponseEntity.ok(leagueAdminService.getCurrentLeagueAdminLeague());
    }
}