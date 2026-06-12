package com.backspacestudios.league_management.referee.controller;

import com.backspacestudios.league_management.referee.dto.BranchLeagueLinkRequest;
import com.backspacestudios.league_management.referee.dto.BranchLeagueLinkResponse;
import com.backspacestudios.league_management.referee.service.BranchLeagueLinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/referee/branches/links")
public class BranchLeagueLinkController {
    private final BranchLeagueLinkService linkService;

    BranchLeagueLinkController(BranchLeagueLinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<BranchLeagueLinkResponse> createLink(@RequestBody BranchLeagueLinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(linkService.createLink(request));
    }

    @DeleteMapping("/{linkId}")
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<Void> removeLink(@PathVariable UUID linkId) {
        linkService.removeLink(linkId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{linkId}/active")
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<BranchLeagueLinkResponse> toggleActive(@PathVariable UUID linkId,
                                                                  @RequestParam boolean active) {
        return ResponseEntity.ok(linkService.toggleActive(linkId, active));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('super_admin', 'league_admin', 'referee', 'user')")
    public ResponseEntity<List<BranchLeagueLinkResponse>> getLinksByBranch(@PathVariable UUID branchId) {
        return ResponseEntity.ok(linkService.getLinksByBranch(branchId));
    }

    @GetMapping("/league/{leagueId}")
    @PreAuthorize("hasAnyRole('super_admin', 'league_admin', 'referee', 'user'  )")
    public ResponseEntity<List<BranchLeagueLinkResponse>> getLinksByLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(linkService.getLinksByLeague(leagueId));
    }
}