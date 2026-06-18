package com.backspacestudios.league_management.team.controller;

import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.team.dto.TeamRequest;
import com.backspacestudios.league_management.team.dto.TeamResponse;
import com.backspacestudios.league_management.team.enums.FinancialStatus;
import com.backspacestudios.league_management.team.enums.TeamStatus;
import com.backspacestudios.league_management.team.service.TeamService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
     private final UserRepository userRepository; 
    TeamController(TeamService teamService, UserRepository userRepository) {
        this.teamService = teamService;
        this.userRepository = userRepository;
    }

    // League admin and super admin can create teams
    @PostMapping
    @PreAuthorize("hasRole('league_admin') or hasRole('super_admin')")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(request));
    }

    @GetMapping("/{teamId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamService.getTeamById(teamId));
    }

    @GetMapping("/league/{leagueId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamResponse>> getTeamsByLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(teamService.getTeamsByLeague(leagueId));
    }

     @PutMapping("/{teamId}")
    @PreAuthorize("hasAnyRole('league_admin','super_admin','team_manager')")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable UUID teamId,
                                                   @Valid @RequestBody TeamRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_team_manager"))) {
            UUID currentUserId = getCurrentUserId();
            teamService.verifyTeamManager(teamId, currentUserId);
        }
        return ResponseEntity.ok(teamService.updateTeam(teamId, request));
    }


    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasRole('super_admin')")  // Only super admin can delete teams
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{teamId}/status")
    @PreAuthorize("hasRole('league_admin') or hasRole('super_admin')")
    public ResponseEntity<TeamResponse> updateTeamStatus(@PathVariable UUID teamId, @RequestParam TeamStatus status) {
        return ResponseEntity.ok(teamService.updateTeamStatus(teamId, status));
    }

    @PatchMapping("/{teamId}/financial-status")
    @PreAuthorize("hasRole('league_admin') or hasRole('super_admin')")
    public ResponseEntity<TeamResponse> updateFinancialStatus(@PathVariable UUID teamId, @RequestParam FinancialStatus financialStatus) {
        return ResponseEntity.ok(teamService.updateFinancialStatus(teamId, financialStatus));
    }
    @GetMapping("/division/{divisionId}/active")
    @PreAuthorize("hasAnyRole('league_admin', 'super_admin', 'team_manager')")
    public ResponseEntity<List<TeamResponse>> getActiveTeamsByDivision(@PathVariable UUID divisionId) {
        return ResponseEntity.ok(teamService.getActiveTeamsByDivision(divisionId));
    }
    @PostMapping("/{teamId}/logo")
    @PreAuthorize("hasAnyRole('team_manager','league_admin','super_admin')")
    public ResponseEntity<TeamResponse> uploadTeamLogo(
            @PathVariable UUID teamId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(teamService.updateTeamLogo(teamId, file));
    }

        private UUID getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        String email = userDetails.getUsername();
        com.backspacestudios.league_management.core.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUserId();
    }
}
