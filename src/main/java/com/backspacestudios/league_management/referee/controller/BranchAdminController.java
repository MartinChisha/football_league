package com.backspacestudios.league_management.referee.controller;

import com.backspacestudios.league_management.core.service.UserService;
import com.backspacestudios.league_management.referee.dto.BranchAdminAssignmentRequest;
import com.backspacestudios.league_management.referee.dto.BranchAdminAssignmentResponse;
import com.backspacestudios.league_management.referee.service.BranchAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/referee/branch-admins")
public class BranchAdminController {
    private final BranchAdminService branchAdminService;
    private final UserService userService;

    BranchAdminController(BranchAdminService branchAdminService, UserService userService) {
        this.branchAdminService = branchAdminService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<Void> assignBranchAdmin(@RequestBody BranchAdminAssignmentRequest request) {
        UUID currentUserId = userService.getCurrentUser().getUserId();
        branchAdminService.assignBranchAdmin(currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/{branchId}")
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<Void> removeBranchAdmin(@PathVariable UUID userId, @PathVariable UUID branchId) {
        UUID currentUserId = userService.getCurrentUser().getUserId();
        branchAdminService.removeBranchAdmin(currentUserId, userId, branchId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/branch/{branchId}")
@PreAuthorize("hasAnyRole('super_admin', 'league_admin')")
public ResponseEntity<List<BranchAdminAssignmentResponse>> getBranchAdmins(@PathVariable UUID branchId) {
    return ResponseEntity.ok(branchAdminService.getBranchAdmins(branchId));
}
}