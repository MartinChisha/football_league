package com.backspacestudios.league_management.referee.controller;

import com.backspacestudios.league_management.competition.dto.AssignRefereeRequest;
import com.backspacestudios.league_management.competition.dto.FixtureRefereeResponse;
import com.backspacestudios.league_management.competition.service.RefereeAssignmentService;
import com.backspacestudios.league_management.core.service.UserService;
import com.backspacestudios.league_management.referee.dto.BranchAdminInfo;
import com.backspacestudios.league_management.referee.dto.BranchRefereeDTO;
import com.backspacestudios.league_management.referee.dto.PendingRequestDTO;
import com.backspacestudios.league_management.referee.service.BranchAdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/branch-admin")
@RequiredArgsConstructor
public class BranchAdminDashboardController {

    private final BranchAdminDashboardService dashboardService;
    private final UserService userService;
    private final RefereeAssignmentService assignmentService;
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('referee')")
    public ResponseEntity<BranchAdminInfo> getCurrentBranchAdmin() {
        UUID userId = userService.getCurrentUser().getUserId();
        return ResponseEntity.ok(dashboardService.getBranchAdminInfo(userId));
    }

    @GetMapping("/referees")
    @PreAuthorize("hasRole('referee')")
    public ResponseEntity<List<BranchRefereeDTO>> getReferees() {
        UUID userId = userService.getCurrentUser().getUserId();
        UUID branchId = dashboardService.getBranchIdByUserId(userId);
        return ResponseEntity.ok(dashboardService.getReferees(branchId));
    }

    @GetMapping("/pending-requests")
    @PreAuthorize("hasRole('referee')")
    public ResponseEntity<List<PendingRequestDTO>> getPendingRequests() {
        UUID userId = userService.getCurrentUser().getUserId();
        UUID branchId = dashboardService.getBranchIdByUserId(userId);
        return ResponseEntity.ok(dashboardService.getPendingRequests(branchId));
    }
    @PostMapping("/assign-referee")
@PreAuthorize("hasRole('referee')")
public ResponseEntity<FixtureRefereeResponse> assignReferee(@RequestBody AssignRefereeRequest request) {
    UUID userId = userService.getCurrentUser().getUserId();
    return ResponseEntity.ok(assignmentService.assignReferee(request, userId));
}

@DeleteMapping("/fixtures/{fixtureId}/referees/{role}")
@PreAuthorize("hasRole('referee')")
public ResponseEntity<Void> removeAssignment(@PathVariable UUID fixtureId, @PathVariable String role) {
    assignmentService.removeAssignment(fixtureId, role);
    return ResponseEntity.noContent().build();
}

@GetMapping("/fixtures/{fixtureId}/referees")
@PreAuthorize("hasRole('referee')")
public ResponseEntity<List<FixtureRefereeResponse>> getAssignments(@PathVariable UUID fixtureId) {
    return ResponseEntity.ok(assignmentService.getAssignmentsForFixture(fixtureId));
}
}