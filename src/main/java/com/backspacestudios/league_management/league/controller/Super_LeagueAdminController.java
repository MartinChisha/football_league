package com.backspacestudios.league_management.league.controller;

import com.backspacestudios.league_management.league.dto.LeagueAdminRequest;
import com.backspacestudios.league_management.league.dto.LeagueAdminResponse;
import com.backspacestudios.league_management.league.service.LeagueAdminService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/league-admins")
@PreAuthorize("hasRole('super_admin')")
public class Super_LeagueAdminController {

    @Autowired
    private LeagueAdminService leagueAdminService;

    @PostMapping("/assign")
    public ResponseEntity<LeagueAdminResponse> assignLeagueAdmin(@Valid @RequestBody LeagueAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leagueAdminService.assignLeagueAdmin(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<LeagueAdminResponse> getLeagueAdminByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(leagueAdminService.getLeagueAdminByUserId(userId));
    }

    @GetMapping("/league/{leagueId}")
    public ResponseEntity<List<LeagueAdminResponse>> getLeagueAdminsByLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(leagueAdminService.getLeagueAdminsByLeague(leagueId));
    }

    @DeleteMapping("/{userId}/{leagueId}")
    public ResponseEntity<Void> removeLeagueAdmin(@PathVariable UUID userId, @PathVariable UUID leagueId) {
        leagueAdminService.removeLeagueAdmin(userId, leagueId);
        return ResponseEntity.noContent().build();
    }
}