package com.backspacestudios.league_management.league.controller;

import com.backspacestudios.league_management.league.dto.DivisionApprovalRequest;
import com.backspacestudios.league_management.league.dto.DivisionResponse;
import com.backspacestudios.league_management.league.service.DivisionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/divisions")
@PreAuthorize("hasRole('super_admin')")
public class SuperAdminDivisionController {

    private final DivisionService divisionService;

    SuperAdminDivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<DivisionResponse>> getPendingDivisions() {
        return ResponseEntity.ok(divisionService.getPendingDivisions());
    }

    @PutMapping("/approve")
    public ResponseEntity<DivisionResponse> approveDivision(@Valid @RequestBody DivisionApprovalRequest request) {
        return ResponseEntity.ok(divisionService.approveDivision(request));
    }
}