package com.backspacestudios.league_management.referee.controller;

import com.backspacestudios.league_management.referee.dto.RefereeBranchRequest;
import com.backspacestudios.league_management.referee.dto.RefereeBranchResponse;
import com.backspacestudios.league_management.referee.service.RefereeBranchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/referee/branches")
public class RefereeBranchController {
    private final RefereeBranchService branchService;

    RefereeBranchController(RefereeBranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<RefereeBranchResponse> createBranch(@RequestBody RefereeBranchRequest request) {
        return new ResponseEntity<>(branchService.createBranch(request), HttpStatus.CREATED);
    }

    @PutMapping("/{branchId}")
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<RefereeBranchResponse> updateBranch(@PathVariable UUID branchId,
                                                              @RequestBody RefereeBranchRequest request) {
        return ResponseEntity.ok(branchService.updateBranch(branchId, request));
    }

    @DeleteMapping("/{branchId}")
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<Void> deleteBranch(@PathVariable UUID branchId) {
        branchService.deleteBranch(branchId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{branchId}")
    @PreAuthorize("hasAnyRole('super_admin', 'league_admin', 'referee', 'user')")
    public ResponseEntity<RefereeBranchResponse> getBranch(@PathVariable UUID branchId) {
        return ResponseEntity.ok(branchService.getBranch(branchId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('super_admin', 'league_admin', 'referee', 'user')")
    public ResponseEntity<List<RefereeBranchResponse>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }
}